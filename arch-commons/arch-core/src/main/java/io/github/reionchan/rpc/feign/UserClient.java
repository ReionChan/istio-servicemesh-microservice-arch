package io.github.reionchan.rpc.feign;

import io.github.reionchan.dto.R;
import io.github.reionchan.dto.users.UserDto;
import io.github.reionchan.rpc.UserRpc;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Reion
 * @date 2024-06-02
 **/
//@FeignClient(name = "arch-users", configuration = FeignClientConfig.class, fallback = UserClientFallback.class)
public interface UserClient extends UserRpc {
    @Override
    @GET
    @Path("/rpc/user/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "根据用户名查询用户", description = "用户名")
    R<UserDto> getUserByName(@PathParam("userName") String userName);
}
