package com.yy.entity;

import java.util.Date;

/**
 * 成功秒杀实体
 */
public class SuccessKilled {

	private long seckillId;

	private long userPhone;

	private short state;

	private Date createTime;

	// 多对一的复合属性
	private Seckill seckill;

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public long getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public Date getCreteTime() {
		return createTime;
	}

	public void setCreteTime(Date creteTime) {
		this.createTime = creteTime;
	}

	public Seckill getSeckill() {
		return seckill;
	}

	public void setSeckill(Seckill seckill) {
		this.seckill = seckill;
	}

	@Override
	public String toString() {
		return "SuccessKilled{" +
				"seckillId=" + seckillId +
				", userPhone=" + userPhone +
				", state=" + state +
				", creteTime=" + createTime +
				", seckill=" + seckill +
				'}';
	}
}
