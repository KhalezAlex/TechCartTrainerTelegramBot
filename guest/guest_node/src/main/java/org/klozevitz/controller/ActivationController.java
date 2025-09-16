package org.klozevitz.controller;

import lombok.RequiredArgsConstructor;
import org.klozevitz.services.implementations.util.ActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ActivationController {
    private final ActivationService activationService;

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        return activationService.activate(id) ?
                ResponseEntity.ok("Регистрация завершена!") :
                ResponseEntity.internalServerError().build();
    }
}
