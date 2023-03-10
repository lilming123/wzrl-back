package org.jeecg.modules.demo.procurement.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spire.pdf.PdfPageBase;
import org.jeecg.modules.system.model.DepartIdModel;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserDepartService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.procurement.entity.ProcurementOrderItem;
import org.jeecg.modules.demo.procurement.entity.ProcurementOrderSupplier;
import org.jeecg.modules.demo.procurement.vo.ProcurementOrderSupplierPage;
import org.jeecg.modules.demo.procurement.service.IProcurementOrderSupplierService;
import org.jeecg.modules.demo.procurement.service.IProcurementOrderItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: ???????????????
 * @Author: jeecg-boot
 * @Date:   2023-01-25
 * @Version: V1.0
 */
@Api(tags="???????????????")
@RestController
@RequestMapping("/procurement/procurementOrderSupplier")
@Slf4j
public class ProcurementOrderSupplierController {
	@Autowired
	private ISysUserDepartService sysUserDepartService;
	 @Autowired
	 private ISysDepartService sysDepartService;
	@Autowired
	private IProcurementOrderSupplierService procurementOrderSupplierService;
	@Autowired
	private IProcurementOrderItemService procurementOrderItemService;
	
	/**
	 * ??????????????????
	 *
	 * @param procurementOrderSupplier
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "???????????????-??????????????????")
	@ApiOperation(value="???????????????-??????????????????", notes="???????????????-??????????????????")
	@GetMapping(value = "/list")
	public Result<IPage<ProcurementOrderSupplier>> queryPageList(ProcurementOrderSupplier procurementOrderSupplier,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ProcurementOrderSupplier> queryWrapper = QueryGenerator.initQueryWrapper(procurementOrderSupplier, req.getParameterMap());
		Page<ProcurementOrderSupplier> page = new Page<ProcurementOrderSupplier>(pageNo, pageSize);
		IPage<ProcurementOrderSupplier> pageList = procurementOrderSupplierService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	 /**
	  * ????????????
	  */
	 @AutoLog(value = "???????????????-????????????")
	 @ApiOperation(value="???????????????-????????????", notes="???????????????-????????????")
	 @GetMapping(value = "/pass")
	 public Result<String> pass(@RequestParam (name="id",required=true) String id){
		 UpdateWrapper<ProcurementOrderSupplier> updateWrapper = new UpdateWrapper<ProcurementOrderSupplier>();
		 updateWrapper.eq("id", id);
		 updateWrapper.set("state","2");
		 procurementOrderSupplierService.update(null,updateWrapper);
		 return Result.OK("???????????????");
	 }
	 /**
	  * ???????????????????????????
	  *
	  */
	 @AutoLog(value = "???????????????????????????")
	 @ApiOperation(value="???????????????????????????", notes="???????????????????????????")
	 @GetMapping(value = "/user")
	 public Result<Map<String,String>> user(){
		 Result<Map<String,String>> result = new Result<Map<String,String>>();
		 Map<String,String> map = new HashMap<String,String>();
		 LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		 List<DepartIdModel> departIdModels = sysUserDepartService.queryDepartIdsOfUser(sysUser.getId());
		 StringBuilder departIds = new StringBuilder(new String());
		 for (DepartIdModel eachDepartIdModel :departIdModels){
			 departIds.append(eachDepartIdModel.getTitle());
		 }
		 System.out.println(departIds);
		 System.out.println(sysUser.getRealname());
		 map.put(sysUser.getRealname(), departIds.toString());
		 result.setResult(map);
		 return result;
	 }
	/**
	 *   ??????
	 *
	 * @param procurementOrderSupplierPage
	 * @return
	 */
	@AutoLog(value = "???????????????-??????")
	@ApiOperation(value="???????????????-??????", notes="???????????????-??????")
    //@RequiresPermissions("procurement:procurement_order_supplier:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProcurementOrderSupplierPage procurementOrderSupplierPage) {
		ProcurementOrderSupplier procurementOrderSupplier = new ProcurementOrderSupplier();
		BeanUtils.copyProperties(procurementOrderSupplierPage, procurementOrderSupplier);
		procurementOrderSupplierService.saveMain(procurementOrderSupplier, procurementOrderSupplierPage.getProcurementOrderItemList());
		return Result.OK("???????????????");
	}
	
	/**
	 *  ??????
	 *
	 * @param procurementOrderSupplierPage
	 * @return
	 */
	@AutoLog(value = "???????????????-??????")
	@ApiOperation(value="???????????????-??????", notes="???????????????-??????")
    //@RequiresPermissions("procurement:procurement_order_supplier:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProcurementOrderSupplierPage procurementOrderSupplierPage) {
		ProcurementOrderSupplier procurementOrderSupplier = new ProcurementOrderSupplier();
		BeanUtils.copyProperties(procurementOrderSupplierPage, procurementOrderSupplier);
		ProcurementOrderSupplier procurementOrderSupplierEntity = procurementOrderSupplierService.getById(procurementOrderSupplier.getId());
		if(procurementOrderSupplierEntity==null) {
			return Result.error("?????????????????????");
		}
		procurementOrderSupplierService.updateMain(procurementOrderSupplier, procurementOrderSupplierPage.getProcurementOrderItemList());
		return Result.OK("????????????!");
	}
	
	/**
	 *   ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "???????????????-??????id??????")
	@ApiOperation(value="???????????????-??????id??????", notes="???????????????-??????id??????")
    //@RequiresPermissions("procurement:procurement_order_supplier:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		procurementOrderSupplierService.delMain(id);
		return Result.OK("????????????!");
	}
	
	/**
	 *  ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "???????????????-????????????")
	@ApiOperation(value="???????????????-????????????", notes="???????????????-????????????")
    //@RequiresPermissions("procurement:procurement_order_supplier:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.procurementOrderSupplierService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("?????????????????????");
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "???????????????-??????id??????")
	@ApiOperation(value="???????????????-??????id??????", notes="???????????????-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<ProcurementOrderSupplier> queryById(@RequestParam(name="id",required=true) String id) {
		ProcurementOrderSupplier procurementOrderSupplier = procurementOrderSupplierService.getById(id);
		if(procurementOrderSupplier==null) {
			return Result.error("?????????????????????");
		}
		return Result.OK(procurementOrderSupplier);

	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "?????????????????????????????????ID??????")
	@ApiOperation(value="???????????????????????????ID??????", notes="?????????????????????-?????????ID??????")
	@GetMapping(value = "/queryProcurementOrderItemByMainId")
	public Result<List<ProcurementOrderItem>> queryProcurementOrderItemListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ProcurementOrderItem> procurementOrderItemList = procurementOrderItemService.selectByMainId(id);
		return Result.OK(procurementOrderItemList);
	}

    /**
    * ??????excel
    *
    * @param request
    * @param procurementOrderSupplier
    */
    //@RequiresPermissions("procurement:procurement_order_supplier:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProcurementOrderSupplier procurementOrderSupplier) {
      // Step.1 ??????????????????????????????
      QueryWrapper<ProcurementOrderSupplier> queryWrapper = QueryGenerator.initQueryWrapper(procurementOrderSupplier, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //??????????????????????????????
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 ??????????????????
      List<ProcurementOrderSupplier> procurementOrderSupplierList = procurementOrderSupplierService.list(queryWrapper);

      // Step.3 ??????pageList
      List<ProcurementOrderSupplierPage> pageList = new ArrayList<ProcurementOrderSupplierPage>();
      for (ProcurementOrderSupplier main : procurementOrderSupplierList) {
          ProcurementOrderSupplierPage vo = new ProcurementOrderSupplierPage();
          BeanUtils.copyProperties(main, vo);
          List<ProcurementOrderItem> procurementOrderItemList = procurementOrderItemService.selectByMainId(main.getId());
          vo.setProcurementOrderItemList(procurementOrderItemList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi ??????Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "?????????????????????");
      mv.addObject(NormalExcelConstants.CLASS, ProcurementOrderSupplierPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("?????????????????????", "?????????:"+sysUser.getRealname(), "???????????????"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * ??????excel????????????
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("procurement:procurement_order_supplier:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // ????????????????????????
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<ProcurementOrderSupplierPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ProcurementOrderSupplierPage.class, params);
              for (ProcurementOrderSupplierPage page : list) {
                  ProcurementOrderSupplier po = new ProcurementOrderSupplier();
                  BeanUtils.copyProperties(page, po);
                  procurementOrderSupplierService.saveMain(po, page.getProcurementOrderItemList());
              }
              return Result.OK("?????????????????????????????????:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("??????????????????:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("?????????????????????");
    }

}
