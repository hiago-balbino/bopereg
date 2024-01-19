package br.com.wes.controller;

import br.com.wes.service.AuthService;
import br.com.wes.vo.v1.security.AccountCredentialsVO;
import br.com.wes.vo.v1.security.TokenVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for Authenticating Users")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Authenticates an user and returns a token",
            description = "Authenticate users to access the application",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = {
                            @Content(schema = @Schema(implementation = TokenVO.class))
                    }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping(
            path = "/signin",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsVO credentials) {
        String invalidClientRequestMessage = "Invalid client request";

        if (checkIfParamsIsNotNull(credentials))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidClientRequestMessage);

        var token = authService.signin(credentials);
        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidClientRequestMessage);

        return token;
    }

    @Operation(
            summary = "Refresh the token",
            description = "Refresh and returns a token for authenticated user",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = {
                            @Content(schema = @Schema(implementation = TokenVO.class))
                    }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PutMapping(
            path = "/refresh/{username}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> refreshToken(@PathVariable("username") String username, @RequestHeader("Authorization") String refreshToken) {
        String invalidClientRequestMessage = "Invalid client request";

        if (checkIfParamsIsNotNull(username, refreshToken))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidClientRequestMessage);

        var token = authService.refreshToken(username, refreshToken);
        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(invalidClientRequestMessage);

        return token;
    }

    private boolean checkIfParamsIsNotNull(AccountCredentialsVO data) {
        return data == null || data.getUsername() == null || data.getUsername().isBlank()
                || data.getPassword() == null || data.getPassword().isBlank();
    }

    private boolean checkIfParamsIsNotNull(String username, String refreshToken) {
        return refreshToken == null || refreshToken.isBlank() || username == null || username.isBlank();
    }
}
