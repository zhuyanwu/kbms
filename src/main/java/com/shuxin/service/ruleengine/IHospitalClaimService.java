package com.shuxin.service.ruleengine;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.shuxin.model.ruleengine.HospitalClaim;
import com.shuxin.model.ruleengine.HospitalClaimDetail;

public interface IHospitalClaimService  extends IService<HospitalClaim> {
	
	public boolean handleHospitalClaimInfo(HospitalClaim hospitalClaim,List<HospitalClaimDetail> hospitalClaimDetails);
	
//	public boolean delHospitalClaimInfo(String diaSerialCode);
	
	public boolean delHospitalClaimInfo(String id);

}
