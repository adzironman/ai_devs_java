package org.adzironman.aidevs.controller;

import lombok.RequiredArgsConstructor;
import org.adzironman.aidevs.model.ownapi.OwnApiRequest;
import org.adzironman.aidevs.model.ownapi.OwnApiResponse;
import org.adzironman.aidevs.task.OwnApiProTask;
import org.adzironman.aidevs.task.OwnApiTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RequestMapping("/")
@RestController
@RequiredArgsConstructor
public class OwnApiController {
    private final OwnApiTask ownApiTask;
    private final OwnApiProTask ownApiProTask;


    @PostMapping()
    public ResponseEntity<OwnApiResponse> answer(@RequestBody OwnApiRequest request) {
        return ResponseEntity.ok(ownApiTask.getResponse(request));
    }
    @PostMapping(path = "apipro")
    public ResponseEntity<OwnApiResponse> answerApiPro(@RequestBody OwnApiRequest request) {
        return ResponseEntity.ok(ownApiProTask.getResponse(request));
    }
}