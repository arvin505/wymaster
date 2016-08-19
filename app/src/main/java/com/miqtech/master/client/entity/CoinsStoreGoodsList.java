package com.miqtech.master.client.entity;

import java.util.List;

/**
 * 金币商城   金币专区和抽奖区的数据集
 * @author Administrator
 *
 */
public class CoinsStoreGoodsList {

	List<CoinsStoreGoods> grobTreasure;  //众筹夺宝数据
	List<CoinsStoreGoods> prizeArea;     //兑奖专区数据

	public List<CoinsStoreGoods> getPrizeArea() {
		return prizeArea;
	}

	public List<CoinsStoreGoods> getGrobTreasure() {
		return grobTreasure;
	}

	public void setGrobTreasure(List<CoinsStoreGoods> grobTreasure) {
		this.grobTreasure = grobTreasure;
	}

	public void setPrizeArea(List<CoinsStoreGoods> prizeArea) {
		this.prizeArea = prizeArea;
	}
}
