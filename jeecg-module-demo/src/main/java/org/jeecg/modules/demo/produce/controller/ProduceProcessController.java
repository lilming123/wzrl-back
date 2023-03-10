package org.jeecg.modules.demo.produce.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
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
import org.jeecg.modules.demo.produce.entity.ProduceProcessMaterial;
import org.jeecg.modules.demo.produce.entity.ProduceProcess;
import org.jeecg.modules.demo.produce.vo.ProduceProcessPage;
import org.jeecg.modules.demo.produce.service.IProduceProcessService;
import org.jeecg.modules.demo.produce.service.IProduceProcessMaterialService;
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
 * @Description: ??????????????????
 * @Author: jeecg-boot
 * @Date:   2023-01-24
 * @Version: V1.0
 */
@Api(tags="??????????????????")
@RestController
@RequestMapping("/produce/produceProcess")
@Slf4j
public class ProduceProcessController {
	@Autowired
	private IProduceProcessService produceProcessService;
	@Autowired
	private IProduceProcessMaterialService produceProcessMaterialService;
	
	/**
	 * ??????????????????
	 *
	 * @param produceProcess
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "??????????????????-??????????????????")
	@ApiOperation(value="??????????????????-??????????????????", notes="??????????????????-??????????????????")
	@GetMapping(value = "/list")
	public Result<IPage<ProduceProcess>> queryPageList(ProduceProcess produceProcess,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ProduceProcess> queryWrapper = QueryGenerator.initQueryWrapper(produceProcess, req.getParameterMap());
		Page<ProduceProcess> page = new Page<ProduceProcess>(pageNo, pageSize);
		page.addOrder(OrderItem.asc("goods_name"));
		page.addOrder(OrderItem.asc("precedence"));
		IPage<ProduceProcess> pageList = produceProcessService.page(page, queryWrapper);

		return Result.OK(pageList);
	}
	
	/**
	 *   ??????
	 *
	 * @param produceProcessPage
	 * @return
	 */
	@AutoLog(value = "??????????????????-??????")
	@ApiOperation(value="??????????????????-??????", notes="??????????????????-??????")
    //@RequiresPermissions("produce:produce_process:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ProduceProcessPage produceProcessPage) {
		ProduceProcess produceProcess = new ProduceProcess();
		BeanUtils.copyProperties(produceProcessPage, produceProcess);
		produceProcessService.saveMain(produceProcess, produceProcessPage.getProduceProcessMaterialList());
		return Result.OK("???????????????");
	}
	
	/**
	 *  ??????
	 *
	 * @param produceProcessPage
	 * @return
	 */
	@AutoLog(value = "??????????????????-??????")
	@ApiOperation(value="??????????????????-??????", notes="??????????????????-??????")
    //@RequiresPermissions("produce:produce_process:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ProduceProcessPage produceProcessPage) {
		ProduceProcess produceProcess = new ProduceProcess();
		BeanUtils.copyProperties(produceProcessPage, produceProcess);
		ProduceProcess produceProcessEntity = produceProcessService.getById(produceProcess.getId());
		if(produceProcessEntity==null) {
			return Result.error("?????????????????????");
		}
		produceProcessService.updateMain(produceProcess, produceProcessPage.getProduceProcessMaterialList());
		return Result.OK("????????????!");
	}
	
	/**
	 *   ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "??????????????????-??????id??????")
	@ApiOperation(value="??????????????????-??????id??????", notes="??????????????????-??????id??????")
    //@RequiresPermissions("produce:produce_process:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		produceProcessService.delMain(id);
		return Result.OK("????????????!");
	}
	
	/**
	 *  ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "??????????????????-????????????")
	@ApiOperation(value="??????????????????-????????????", notes="??????????????????-????????????")
    //@RequiresPermissions("produce:produce_process:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.produceProcessService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("?????????????????????");
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "??????????????????-??????id??????")
	@ApiOperation(value="??????????????????-??????id??????", notes="??????????????????-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<ProduceProcess> queryById(@RequestParam(name="id",required=true) String id) {
		ProduceProcess produceProcess = produceProcessService.getById(id);
		if(produceProcess==null) {
			return Result.error("?????????????????????");
		}
		return Result.OK(produceProcess);

	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "?????????????????????????????????ID??????")
	@ApiOperation(value="???????????????????????????ID??????", notes="?????????????????????-?????????ID??????")
	@GetMapping(value = "/queryProduceProcessMaterialByMainId")
	public Result<List<ProduceProcessMaterial>> queryProduceProcessMaterialListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ProduceProcessMaterial> produceProcessMaterialList = produceProcessMaterialService.selectByMainId(id);
		return Result.OK(produceProcessMaterialList);
	}

    /**
    * ??????excel
    *
    * @param request
    * @param produceProcess
    */
    //@RequiresPermissions("produce:produce_process:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProduceProcess produceProcess) {
      // Step.1 ??????????????????????????????
      QueryWrapper<ProduceProcess> queryWrapper = QueryGenerator.initQueryWrapper(produceProcess, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //??????????????????????????????
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 ??????????????????
      List<ProduceProcess> produceProcessList = produceProcessService.list(queryWrapper);

      // Step.3 ??????pageList
      List<ProduceProcessPage> pageList = new ArrayList<ProduceProcessPage>();
      for (ProduceProcess main : produceProcessList) {
          ProduceProcessPage vo = new ProduceProcessPage();
          BeanUtils.copyProperties(main, vo);
          List<ProduceProcessMaterial> produceProcessMaterialList = produceProcessMaterialService.selectByMainId(main.getId());
          vo.setProduceProcessMaterialList(produceProcessMaterialList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi ??????Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "????????????????????????");
      mv.addObject(NormalExcelConstants.CLASS, ProduceProcessPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("????????????????????????", "?????????:"+sysUser.getRealname(), "??????????????????"));
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
    //@RequiresPermissions("produce:produce_process:importExcel")
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
              List<ProduceProcessPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ProduceProcessPage.class, params);
              for (ProduceProcessPage page : list) {
                  ProduceProcess po = new ProduceProcess();
                  BeanUtils.copyProperties(page, po);
                  produceProcessService.saveMain(po, page.getProduceProcessMaterialList());
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
