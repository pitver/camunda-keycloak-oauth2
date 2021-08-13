package ru.vershinin.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vershinin.model.ProcessConstants;
import ru.vershinin.model.ProcessInstanceInfo;

import java.util.UUID;

@RestController
@RequestMapping("/rest/startprocess")
public class StartController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RuntimeService runtimeService;
    private final ProcessInstanceInfo processInstanceInfo;

    public StartController(RuntimeService runtimeService, ProcessInstanceInfo processInstanceInfo) {
        this.runtimeService = runtimeService;
        this.processInstanceInfo = processInstanceInfo;
    }


    @PostMapping
    public ResponseEntity<ProcessInstanceInfo> placeStartPOST()  {

        return ResponseEntity.status(201).body(placeStart());
    }


    /**
     * we need a method returning the {@link ProcessInstance} to allow for easier tests,
     * that's why I separated the REST method (without return) from the actual implementaion (with return value)
     *
     * @return
     */
    public ProcessInstanceInfo placeStart() {
       ProcessInstance processInstance= runtimeService.startProcessInstanceByKey(ProcessConstants.PROCESS_KEY_order, UUID.randomUUID().toString());
        processInstanceInfo.setBusinessKey(processInstance.getBusinessKey());
        processInstanceInfo.setProcessInstanceId(processInstance.getProcessInstanceId());

        logger.info(new StringBuilder().append("businessKey ").append(processInstance.getBusinessKey()).toString());
        logger.info(new StringBuilder().append("processInstanceId ").append(processInstance.getProcessInstanceId()).toString());
        return processInstanceInfo;
    }
}
