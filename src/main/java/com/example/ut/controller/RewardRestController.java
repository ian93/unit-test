package com.example.ut.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ut.service.RewardService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(value = "/reward")
@RestController
public class RewardRestController {

    private final RewardService rewardService;

    @GetMapping(value = "/total")
    public Map<String, Integer> reward() {
        return Map.of("total", rewardService.getPersonalTotalReward());
    }
}
