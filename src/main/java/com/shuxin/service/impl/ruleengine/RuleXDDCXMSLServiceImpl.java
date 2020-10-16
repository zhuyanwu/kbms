package com.shuxin.service.impl.ruleengine;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuxin.commons.utils.ToolUtils;
import com.shuxin.mapper.ruleengine.HospitalClaimDetailMapper;
import com.shuxin.mapper.ruleengine.RuleXDDCXMSLMapper;
import com.shuxin.model.RuleTableInfo;
import com.shuxin.model.ruleengine.HospitalClaim;
import com.shuxin.model.ruleengine.HospitalClaimDetail;
import com.shuxin.model.ruleengine.ViolationDetail;
import com.shuxin.service.ruleengine.IAnalysisRuleService;
/**
 * 限定单次项目数量
 *
 */
@Service
public class RuleXDDCXMSLServiceImpl implements IAnalysisRuleService {
	
	@Autowired
	private RuleXDDCXMSLMapper ruleXDDCXMSLMapper;
	
	@Autowired
	private HospitalClaimDetailMapper hospitalClaimDetailMapper;

	@Override
	public List<ViolationDetail> executeRule(RuleTableInfo rule, HospitalClaim hospitalClaim,
			List<HospitalClaimDetail> hospitalClaimDetails) {
		
		List<ViolationDetail> list= null;
		ViolationDetail violationDetail =null;
		List<HospitalClaimDetail> projectList = new ArrayList<HospitalClaimDetail>();
		List<HospitalClaimDetail> projectListTemp = new ArrayList<HospitalClaimDetail>();
		List<String> productCodeTemp =new ArrayList<String>();
		
		for(HospitalClaimDetail hospitalClaimDetail:hospitalClaimDetails)
		{
			//只审核项目，不审核药品
			if("1".equals(hospitalClaimDetail.getThrCatType()))
			{
				continue;
			}			
			
			if(!productCodeTemp.contains(hospitalClaimDetail.getProductCode())){
				projectListTemp.add(hospitalClaimDetail);
				productCodeTemp.add(hospitalClaimDetail.getProductCode());
				
			}
			
			projectList.add(hospitalClaimDetail);				
		}
		
		if(projectList.size()==0)
		{
			return null;
		}
		
		List<Map<String, String>> singleProjectLimitList = ruleXDDCXMSLMapper.selectSingleProjectLimitInfo(projectListTemp);
		
		if(singleProjectLimitList.size()==0)
		{
			return null;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("patCode", hospitalClaim.getPatCode());
		
		for(Map<String, String> singleProjectLimitMap:singleProjectLimitList)
		{
			int timeInterval = Integer.parseInt(singleProjectLimitMap.get("SJQJT"));
			int limitCount = Integer.parseInt(singleProjectLimitMap.get("XDSLGS"));
			for(HospitalClaimDetail hospitalClaimDetail:projectList)
			{
				if(!hospitalClaimDetail.getProductCode().equals(singleProjectLimitMap.get("PROJECT_CODE")))
				{
					continue;
				}
				
				float count= hospitalClaimDetail.getPnumber();
				if(timeInterval>1)
				{
					paramMap.put("productCode", hospitalClaimDetail.getProductCode());
					paramMap.put("toDate", format.format(hospitalClaimDetail.getServiceDate()));
					paramMap.put("intervalDays", singleProjectLimitMap.get("SJQJT"));
					BigDecimal frequentlyNumber = hospitalClaimDetailMapper.selectFrequentlyDrugNumber(paramMap);
					count = frequentlyNumber.intValue();
				}
				
				if(count>limitCount)
				{
					violationDetail=ToolUtils.getViolationDetail(rule, hospitalClaim, hospitalClaimDetail, singleProjectLimitMap.get("TSXX"));
					if(list==null)
					{
						list= new ArrayList<ViolationDetail>();
					}
					list.add(violationDetail);
				}
			}
		}
		
		
		return list;
	}

}
