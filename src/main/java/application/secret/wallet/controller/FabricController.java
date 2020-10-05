package application.secret.wallet.controller;

/**
 * Created by Pranav on Tue Dec 10 10:54:13 ICT 2019
 */

import application.secret.wallet.model.InvokeRequest;
import application.secret.wallet.service.FabricService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;

@RestController
@EnableAutoConfiguration
@EnableSwagger2
@Api(tags = "K8 Secrets REST APIs", value = "")
@RequestMapping(value = "/fabric-services")
public class FabricController {

    final Logger log = LoggerFactory.getLogger(FabricController.class);

    @Autowired
    private FabricService fabricService;

    @ApiOperation(value = "query ledger", notes = "query transaction in ledger")
    @GetMapping(value = "/query")
    public String query(
            @ApiParam(name = "channelName", value = "Channel name", required = true) @RequestParam("channelName") String channelName,
            @ApiParam(name = "chaincodeName", value = "Chaincode name", required = true) @RequestParam("chaincodeName") String chaincodeName,
            @ApiParam(name = "chaincodeMethod", value = "Calling function name in chaincode", required = true) @RequestParam("chaincodeMethod") String chaincodeMethod,
            @ApiParam(name = "params", value = "array containing query params", required = false) @RequestParam(value="params", required = false, defaultValue = "") String... params) throws Exception
    {
        log.info("received request to query data in legder. channel: {}, chaincodeName: {}, chaincodeMethod: {}, params: {}", channelName, chaincodeName, chaincodeMethod, Arrays.toString(params));
        String response = fabricService.evaluateTransaction(channelName, chaincodeName,chaincodeMethod, params);
        log.info("query \n Response : {}", response);
        return response;
    }

    @ApiOperation(value = "invoke transaction", notes = "invoke transaction in ledger")
    @PostMapping(value = "/invoke")
    public String invoke(@Valid @RequestBody(required = true) InvokeRequest invokeRequest, HttpServletRequest request) throws Exception {
        log.info("Received request to save data in ledger. \n Request : {} ", invokeRequest);
        String response = fabricService.submitTransaction(invokeRequest, request);
        log.info("invoke \n Response : {}", response);
        return response;
    }

}
