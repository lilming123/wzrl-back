package org.jeecg.modules.demo.worker.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.worker.entity.WorkerSalary;
import org.jeecg.modules.demo.worker.service.IWorkerSalaryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 员工薪资管理
 * @Author: jeecg-boot
 * @Date:   2023-01-22
 * @Version: V1.0
 */
@Api(tags="员工薪资管理")
@RestController
@RequestMapping("/worker/workerSalary")
@Slf4j
public class WorkerSalaryController extends JeecgController<WorkerSalary, IWorkerSalaryService> {
	@Autowired
	private IWorkerSalaryService workerSalaryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param workerSalary
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "员工薪资管理-分页列表查询")
	@ApiOperation(value="员工薪资管理-分页列表查询", notes="员工薪资管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WorkerSalary>> queryPageList(WorkerSalary workerSalary,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WorkerSalary> queryWrapper = QueryGenerator.initQueryWrapper(workerSalary, req.getParameterMap());
		Page<WorkerSalary> page = new Page<WorkerSalary>(pageNo, pageSize);
		IPage<WorkerSalary> pageList = workerSalaryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param workerSalary
	 * @return
	 */
	@AutoLog(value = "员工薪资管理-添加")
	@ApiOperation(value="员工薪资管理-添加", notes="员工薪资管理-添加")
	//@RequiresPermissions("worker:worker_salary:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WorkerSalary workerSalary) {
		workerSalaryService.save(workerSalary);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param workerSalary
	 * @return
	 */
	@AutoLog(value = "员工薪资管理-编辑")
	@ApiOperation(value="员工薪资管理-编辑", notes="员工薪资管理-编辑")
	//@RequiresPermissions("worker:worker_salary:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WorkerSalary workerSalary) {
		workerSalaryService.updateById(workerSalary);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "员工薪资管理-通过id删除")
	@ApiOperation(value="员工薪资管理-通过id删除", notes="员工薪资管理-通过id删除")
	//@RequiresPermissions("worker:worker_salary:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		workerSalaryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "员工薪资管理-批量删除")
	@ApiOperation(value="员工薪资管理-批量删除", notes="员工薪资管理-批量删除")
	//@RequiresPermissions("worker:worker_salary:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.workerSalaryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "员工薪资管理-通过id查询")
	@ApiOperation(value="员工薪资管理-通过id查询", notes="员工薪资管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WorkerSalary> queryById(@RequestParam(name="id",required=true) String id) {
		WorkerSalary workerSalary = workerSalaryService.getById(id);
		if(workerSalary==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(workerSalary);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param workerSalary
    */
    //@RequiresPermissions("worker:worker_salary:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WorkerSalary workerSalary) {
        return super.exportXls(request, workerSalary, WorkerSalary.class, "员工薪资管理");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("worker:worker_salary:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WorkerSalary.class);
    }

}
