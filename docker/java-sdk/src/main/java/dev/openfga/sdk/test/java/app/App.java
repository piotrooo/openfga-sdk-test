package dev.openfga.sdk.test.java.app;

import dev.openfga.sdk.api.OpenFgaApi;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.CreateStoreResponse;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.concurrent.ExecutionException;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.http.HttpStatus.OK;

public class App {
    public static void main(String[] args) throws FgaInvalidParameterException {
        Configuration configuration = new Configuration()
                .apiUrl("http://openfga:8080");
        OpenFgaApi openFgaApi = new OpenFgaApi(configuration);

        Javalin.create(config ->
                        config.router.apiBuilder(() -> {
                            path("/stores", () -> {
                                post(ctx -> createStore(ctx, openFgaApi));
                                get(ctx -> listStores(ctx, openFgaApi));
                            });
                        })
                )
                .get("/health", ctx -> ctx.status(OK).result("OK"))
                .start(9000);
    }

    private static void createStore(Context ctx, OpenFgaApi openFgaApi) throws InterruptedException, ExecutionException, ApiException, FgaInvalidParameterException {
        CreateStoreRequest request = ctx.bodyAsClass(CreateStoreRequest.class);
        ApiResponse<CreateStoreResponse> response = openFgaApi.createStore(request).get();
        ctx.status(response.getStatusCode()).json(response.getData());
    }

    private static void listStores(Context ctx, OpenFgaApi openFgaApi) {
    }
}
