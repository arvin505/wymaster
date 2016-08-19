package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.listviewfilter.PinnedHeaderAdapter;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.PingYinUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.IndexBarView;
import com.miqtech.master.client.view.PinnedHeaderListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LocationCityActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
    private EditText edtSearch;
    private ImageView ivCancelSearch;
    private PinnedHeaderListView lvCity;
    private ProgressBar pbLoadingView;
    private TextView tvEmpty, tvCurrentCity, tvLocCity;
    private RelativeLayout rlLocationCity, rlCurrentCity;
    PinnedHeaderAdapter adapter;
    private ArrayList<City> cities = new ArrayList<City>();
    private List<Integer> mListSectionPos = new ArrayList<Integer>();
    private List<Object> sortCities = new ArrayList<Object>();
    private ImageView back;
    private Context context;
    private static final int REQUEST_CITY_LIST = 1;
    protected void init() {
        super.init();
        setContentView(R.layout.activity_locationcity);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        pbLoadingView = (ProgressBar) findViewById(R.id.loading_view);
        tvEmpty = (TextView) findViewById(R.id.empty_view);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        lvCity = (PinnedHeaderListView) findViewById(R.id.lvCity);
        tvLocCity = (TextView) findViewById(R.id.tvLocationCity);
        tvCurrentCity = (TextView) findViewById(R.id.tvCurrentCity);
        rlLocationCity = (RelativeLayout) findViewById(R.id.rlLocationCity);
        rlCurrentCity = (RelativeLayout) findViewById(R.id.rlCurrentCity);

        setLeftIncludeTitle("城市选择");
        //getLeftBtn().setOnClickListener(this);

        lvCity.setOnItemClickListener(this);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);

        String lastCityStr = PreferencesUtil.getLastRecreationCity(context);
        if (TextUtils.isEmpty(lastCityStr)) {
            if (Constant.currentCity != null) {
                tvCurrentCity.setText(Constant.currentCity.getName());
                rlCurrentCity.setTag(Constant.currentCity);
            } else {
                tvCurrentCity.setText("定位失败");
            }
        } else {
            City lastCity = GsonUtil.getBean(lastCityStr, City.class);
            tvCurrentCity.setText(lastCity.getName());
            rlCurrentCity.setTag(lastCity);
        }

        if (Constant.locCity != null) {
            tvLocCity.setText(Constant.locCity.getName());
            rlLocationCity.setTag(Constant.locCity);
        } else {
            tvLocCity.setText("定位失败");
        }

        rlLocationCity.setOnClickListener(this);
        rlCurrentCity.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        loadCities();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        edtSearch.addTextChangedListener(filterTextWatcher);
        super.onPostCreate(savedInstanceState);
    }

    private void setListAdaptor() {
        // create instance of PinnedHeaderAdapter and set adapter to list view
        adapter = new PinnedHeaderAdapter(this, sortCities, mListSectionPos);
        lvCity.setAdapter(adapter);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // set header view
        View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, lvCity, false);
        lvCity.setPinnedHeaderView(pinnedHeaderView);

        // set index bar view
        IndexBarView indexBarView = (IndexBarView) inflater.inflate(R.layout.index_bar_view, lvCity, false);
        indexBarView.setData(lvCity, sortCities, mListSectionPos);
        lvCity.setIndexBarView(indexBarView);

        // set preview text view
        View previewTextView = inflater.inflate(R.layout.preview_view, lvCity, false);
        lvCity.setPreviewView(previewTextView);

        // for configure pinned header view on scroll change
        lvCity.setOnScrollListener(adapter);
    }

    private void loadCities() {
        showLoading();
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CITY_LIST, null, HttpConstant.CITY_LIST);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (adapter != null && str != null)
                adapter.getFilter().filter(str);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        super.onSuccess(object, method);
        try {
            String obj = object.getJSONArray("object").toString();
            if (HttpConstant.CITY_LIST.equals(method)) {
                List<City> citysNew = new Gson().fromJson(obj.toString(), new TypeToken<List<City>>() {
                }.getType());
                cities.addAll(citysNew);
                new Poplulate().execute(cities);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
    }

    public class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.
            String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                boolean isChinese = PingYinUtil.isChinese(constraintStr);
                if (isChinese) {
                    ArrayList<City> filterItems = new ArrayList<City>();
                    //
                    synchronized (this) {
                        for (City city : cities) {
                            if (city.getName().indexOf(constraintStr) != -1) {
                                filterItems.add(city);
                            }
                        }
                        result.count = filterItems.size();
                        result.values = filterItems;
                    }
                } else {
                    ArrayList<City> filterItems = new ArrayList<City>();

                    //
                    synchronized (this) {
                        for (City city : cities) {
                            String[] letters = city.getPinyin().split(" ");
                            if (Utils.replaceBlank(city.getPinyin()).indexOf(constraintStr) != -1) {
                                filterItems.add(city);
                            } else {
                                String temp = "";
                                for (int i = 0; i < letters.length; i++) {
                                    temp += letters[i].charAt(0);
                                }
                                if (temp.indexOf(constraintStr) != -1) {
                                    filterItems.add(city);
                                }
                            }
                        }
                        result.count = filterItems.size();
                        result.values = filterItems;
                    }
                }
            } else {
                synchronized (this) {
                    result.count = cities.size();
                    result.values = cities;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<City> filtered = (ArrayList<City>) results.values;
            setIndexBarViewVisibility(constraint.toString());
            // sort array and extract sections in background Thread
            new Poplulate().execute(filtered);
        }

    }

    private void setIndexBarViewVisibility(String constraint) {
        // hide index bar for search results
        if (constraint != null && constraint.length() > 0) {
            lvCity.setIndexBarVisibility(false);
        } else {
            lvCity.setIndexBarVisibility(true);
        }
    }

    private class Poplulate extends AsyncTask<ArrayList<City>, Void, Void> {

        private void showLoading(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        private void showContent(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }

        private void showEmptyText(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            // show loading indicator
            showLoading(lvCity, pbLoadingView, tvEmpty);
            setListAdaptor();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<City>... params) {
            sortCities.clear();
            mListSectionPos.clear();
            ArrayList<City> items = params[0];
            if (cities.size() > 0) {

                // NOT forget to sort array
                Collections.sort(items);

                String prev_section = "";
                for (City currentCity : items) {
                    String current_section = currentCity.getPinyin().substring(0, 1).toUpperCase(Locale.getDefault());

                    if (!prev_section.equals(current_section)) {
                        sortCities.add(current_section);
                        sortCities.add(currentCity);
                        // array list of section positions
                        mListSectionPos.add(sortCities.indexOf(current_section));
                        prev_section = current_section;
                    } else {
                        sortCities.add(currentCity);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                if (sortCities.size() <= 0) {
                    showEmptyText(lvCity, pbLoadingView, tvEmpty);
                } else {
                    setListAdaptor();
                    showContent(lvCity, pbLoadingView, tvEmpty);
                }
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onClick(View v) {
        City city;
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.rlLocationCity:
                city = (City) rlLocationCity.getTag();
                if (city != null) {
                    boolean fromEditData = getIntent().getBooleanExtra("fromEditData", false);
                    Intent intent = new Intent();
                    if (fromEditData) {
                        intent.setClass(this, EditDataActivity.class);
                        intent.putExtra("city", city);
                        setResult(RESULT_OK, intent);
                        String cityStr = new Gson().toJson((City) city);
                        PreferencesUtil.setLastRecreationCity(cityStr, context);
                        finish();
                    } else {
                        intent.setClass(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Constant.currentCity = city;
                        setResult(RESULT_OK, intent);
                        String cityStr = new Gson().toJson((City) city);
                        PreferencesUtil.setLastRecreationCity(cityStr, context);
                        finish();
                    }

                }
                break;
            case R.id.rlCurrentCity:
                city = (City) rlCurrentCity.getTag();
                if (city != null) {
                    boolean fromEditData = getIntent().getBooleanExtra("fromEditData", false);
                    Intent intent = new Intent();
                    if (fromEditData) {
                        intent.setClass(this, EditDataActivity.class);
                        intent.putExtra("city", city);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        intent.setClass(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Constant.currentCity = city;
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean fromEditData = getIntent().getBooleanExtra("fromEditData", false);
        Object cityObj = sortCities.get(position);
        Intent intent = new Intent();
        if (cityObj instanceof City) {
            if (fromEditData) {
                intent.setClass(this, EditDataActivity.class);
                intent.putExtra("city", (City) cityObj);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Constant.currentCity = (City) cityObj;
                intent.setClass(this, MainActivity.class);
                setResult(RESULT_OK, intent);
                String cityStr = new Gson().toJson((City) cityObj);
                PreferencesUtil.setLastRecreationCity(cityStr, context);
                finish();
            }
        }
    }
}
