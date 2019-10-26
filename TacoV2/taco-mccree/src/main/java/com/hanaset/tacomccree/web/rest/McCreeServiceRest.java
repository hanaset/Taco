package com.hanaset.tacomccree.web.rest;

import com.hanaset.tacomccree.service.McCreeUpbitService;
import com.hanaset.tacomccree.web.rest.support.McCreeApiRestSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@Api(tags = "Reaper 서비스 작동 API", value = "Reaper Service Operate API")
@RestController
@RequestMapping("/service")
public class McCreeServiceRest extends McCreeApiRestSupport {

    private final McCreeUpbitService mcCreeUpbitService;

    public McCreeServiceRest(McCreeUpbitService mcCreeUpbitService) {
        this.mcCreeUpbitService = mcCreeUpbitService;
    }


    @ApiOperation(value =
            "서비스 시작"
    )
    @PostMapping("/start")
    public ResponseEntity start() {
        mcCreeUpbitService.startService();
        return success("OK");
    }

    @ApiOperation(value =
            "서비스 종료"
    )
    @PostMapping("/finish")
    public ResponseEntity finish() {
        mcCreeUpbitService.finishService();
        return success("OK");
    }

//    @ApiOperation(value =
//            "잔고 조회"
//    )
//    @GetMapping("/balance")
//    public ResponseEntity getBalance() {
//        //return success(reaperProbitService.getBalance());
//        return success(reaperOkexService.getAccount());
//    }
//
//    @ApiOperation(value =
//            "마켓 조회"
//    )
//    @GetMapping("/market")
//    public ResponseEntity getMarket() {
//        return success(reaperProbitService.getMarket());
//    }
//
//    @ApiOperation(value =
//            "수익 조회"
//    )
//    @GetMapping("/profit")
//    public ResponseEntity getProfit() {
//        reaperProbitService.getProfit();
//        return success("OK");
//    }
}
