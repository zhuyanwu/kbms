package com.shuxin.service.ruleengine;

import java.util.List;
import java.util.Map;

import com.shuxin.model.ruleengine.HospitalClaim;
import com.shuxin.model.ruleengine.HospitalClaimDetail;
import com.shuxin.model.ruleengine.ViolationDetail;

public interface IViolationDetailService{
	
	public void editViolationDetail(HospitalClaim hospitalClaim,List<HospitalClaimDetail> hospitalClaimDetails,List<ViolationDetail> violationDetails);

	public void deleteViolationDetail(String id);
	
	public void addViolationResult(Map<String, String> paramMap);
}
