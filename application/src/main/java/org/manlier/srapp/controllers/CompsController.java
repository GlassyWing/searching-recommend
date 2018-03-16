package org.manlier.srapp.controllers;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.manlier.srapp.constraints.Status;
import org.manlier.srapp.dto.ResponseResult;
import org.manlier.srapp.dto.result.CompsAddResult;
import org.manlier.srapp.dto.result.CompsQueryResult;
import org.manlier.srapp.dto.result.CompsUpdateResult;
import org.manlier.srapp.constraints.Error;
import org.manlier.srapp.dto.result.ErrorResult;
import org.manlier.srapp.entities.Component;
import org.manlier.srapp.services.CompsService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.manlier.srapp.constraints.Limits.*;

@RestController
public class CompsController {

    private CompsService service;
    private Logger logger = Logger.getLogger(getClass());

    @Inject
    public CompsController(CompsService service) {
        this.service = service;
    }

    /**
     * 按条件过滤查找构件
     *
     * @param desc 构件描述
     * @param rows 欲得到的构件数
     * @return 响应结果
     */
    @RequestMapping(value = "/api/v1/comps", method = RequestMethod.GET)
    public ResponseResult searchComps(@RequestParam("desc") String desc
            , @RequestParam(value = "rows", defaultValue = "10") int rows) {
        ResponseResult.Builder builder = new ResponseResult.Builder();
        builder.addResponseHeaderParam("desc", desc)
                .addResponseHeaderParam("rows", rows);
        logger.info("Try to search components with filter param: desc=" + desc + ", " + "rows=" + rows);
        rows = Math.min(rows, MAX_COMPS_QUERY_NUM);
        builder.startTiming();
        try {
            logger.info("Searching components.");
            List<Component> components = service.searchComps(desc, rows);
            builder.setResponseHeaderStatus(Status.SUCCESS.code())
                    .setResponse(new CompsQueryResult(components));
            logger.info("There are " + components.size() + " components found.");
        } catch (IOException | SolrServerException e) {
            logger.error("An error occurred while searching.", e);
            builder.setResponseHeaderStatus(Status.FATAL.code())
                    .setResponse(new ErrorResult(Error.valueOf(Status.INTERNAL_SERVER_ERROR)));
        }
        builder.endTime();
        return builder.build();
    }

    /**
     * 按构件id查询构件
     *
     * @param id 构件id
     * @return 响应结果
     */
    @RequestMapping(value = "/api/v1/comps/{id:.+}", method = RequestMethod.GET)
    public ResponseResult searchComp(@PathVariable("id") String id) {
        ResponseResult.Builder builder = new ResponseResult.Builder();
        builder.setPathVariable("/api/v1/comps/" + id);
        logger.info("Try to find specified component with id: " + id);
        builder.startTiming();
        try {
            Optional<Component> component = service.searchComp(id);
            if (component.isPresent()) {
                builder.setResponseHeaderStatus(Status.SUCCESS.code())
                        .setResponse(new CompsQueryResult(component.get()));
                logger.info("Component found.");
            } else {
                builder.setResponseHeaderStatus(Status.FATAL.code())
                        .setResponse(new ErrorResult(Error.RESOURCE_DOES_NOT_EXIST));
                logger.info("Component does not exist.");
            }
        } catch (IOException e) {
            logger.error("An error occurred while searching.", e);
            builder.setResponseHeaderStatus(Status.FATAL.code())
                    .setResponse(new ErrorResult(Error.valueOf(Status.INTERNAL_SERVER_ERROR)));
        }
        builder.endTime();
        return builder.build();
    }

    /**
     * 添加构件
     *
     * @param component 构件
     * @return 响应结果
     */
    @RequestMapping(value = "/api/v1/comps", method = RequestMethod.POST)
    public ResponseResult addComp(@RequestBody Component component) {
        ResponseResult.Builder builder = new ResponseResult.Builder();
        builder.startTiming();
        try {
            Optional<Component> optComp = service.searchComp(component.getId());
            if (optComp.isPresent()) {
                logger.info("The component to be added already exists, adding component can be fail");
                builder.setResponseHeaderStatus(Status.FATAL.code())
                        .setResponse(new ErrorResult(Error.RESOURCE_ALREADY_EXIST));
            } else {
                service.addComp(component);
                builder.setResponseHeaderStatus(Status.SUCCESS.code())
                        .setResponse(new CompsAddResult(component));
                logger.info("Component added.");
            }
        } catch (IOException e) {
            logger.error("An error occurred while adding, addition failed", e);
            builder.setResponseHeaderStatus(Status.FATAL.code())
                    .setResponse(new ErrorResult(Error.valueOf(Status.INTERNAL_SERVER_ERROR)));
        }
        builder.endTime();
        return builder.build();
    }

    /**
     * 根据构件id删除一个构件
     *
     * @param id 构件id
     * @return 响应结果
     */
    @RequestMapping(value = "/api/v1/comps/{id:.+}", method = RequestMethod.DELETE)
    public ResponseResult deleteComp(@PathVariable("id") String id) {
        ResponseResult.Builder builder = new ResponseResult.Builder();
        builder.startTiming();
        try {
            logger.info("Deleting component: " + id);
            service.deleteComp(id);
            builder.setResponseHeaderStatus(Status.SUCCESS.code());
            logger.info("Component: " + id + " has deleted.");
        } catch (IOException e) {
            logger.error("An error occurred while deleting, delete failed.", e);
            builder.setResponseHeaderStatus(Status.FATAL.code())
                    .setResponse(new ErrorResult(Error.valueOf(Status.INTERNAL_SERVER_ERROR)));
        }
        builder.endTime();
        return builder.build();
    }

    /**
     * 更新一个构件
     *
     * @param component 构件
     * @return 响应结果
     */
    @RequestMapping(value = "/api/v1/comps", method = RequestMethod.PATCH)
    public ResponseResult updateComp(@RequestBody Component component) {
        ResponseResult.Builder builder = new ResponseResult.Builder();
        builder.startTiming();
        try {
            Optional<Component> oldComp = service.updateComp(component);
            if (oldComp.isPresent()) {
                logger.info("Component: " + component.getId() + " updated");
                builder.setResponseHeaderStatus(Status.SUCCESS.code())
                        .setResponse(new CompsUpdateResult(oldComp.get(), component));
            } else {
                logger.info("The component to be update does not exists, updating can be fail.");
                builder.setResponseHeaderStatus(Status.FATAL.code())
                        .setResponse(new ErrorResult(Error.RESOURCE_DOES_NOT_EXIST));
            }
        } catch (IOException e) {
            logger.error("An error occurred while updating, update failed.", e);
            builder.setResponseHeaderStatus(Status.FATAL.code())
                    .setResponse(new ErrorResult(Error.valueOf(Status.INTERNAL_SERVER_ERROR)));
        }
        builder.endTime();
        return builder.build();
    }
}
