package com.hanaset.tacoreaper.web.rest;

import com.hanaset.tacoreaper.service.ReaperProbitService;
import com.hanaset.tacoreaper.web.rest.support.ReaperApiRestSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Reaper 서비스 작동 API", value = "Reaper Service Operate API")
@RestController
@RequestMapping("/service")
public class ReaperServiceRest extends ReaperApiRestSupport {

    private final ReaperProbitService reaperProbitService;

    public ReaperServiceRest(ReaperProbitService reaperProbitService) {
        this.reaperProbitService = reaperProbitService;
    }


    @ApiOperation(value =
        "서비스 시작"
    )
    @PostMapping("/start")
    public ResponseEntity start(@RequestParam String pair) {
        reaperProbitService.serviceStart(pair);
        return success("OK");
    }

    @ApiOperation(value =
        "서비스 종료"
    )
    @PostMapping("/finish")
    public ResponseEntity finish() {
        reaperProbitService.serviceFinish();
        return success("OK");
    }

    @ApiOperation(value =
        "잔고 조회"
    )
    @GetMapping("/balance")
    public ResponseEntity getBalance() {
        return success(reaperProbitService.getBalance());
    }

    @ApiOperation(value =
            "마켓 조회"
    )
    @GetMapping("/market")
    public ResponseEntity getMarket() {
        return success(reaperProbitService.getMarket());
    }

    @ApiOperation(value =
        "수익 조회"
    )
    @GetMapping("/profit")
    public ResponseEntity getProfit() {
        reaperProbitService.getProfit();
        return success("OK");
    }
}
