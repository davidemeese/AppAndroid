package com.dam.tfg.interfaces;

import com.dam.tfg.model.InfraccionData;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {
    @GET()
    Call<JsonObject> getVelMax(@Url String url);

    @FormUrlEncoded
    @POST("kafkaproducer/infracciones")
    Call<String> sendKafka(
            @Field("lat") String lat,
            @Field("lon") String lon,
            @Field("mat") String matricula,
            @Field("vel") String velocidad
    );

    @GET("/usuarios/{userId}/matricula")
    Call<String> getMatriculaByUserId(@Header("Authorization") String token, @Path("userId") String userId);

    @POST("/usuarios/{userId}/matricula")
    Call<Void> setMatriculaByUserId(@Header("Authorization") String token, @Path("userId") String userId, @Body String matricula);

    @GET("/usuarios/{userId}/infracciones")
    Call<List<InfraccionData>> getInfracciones(@Header("Authorization") String token, @Path("userId") String userId);

    @POST("usuarios/{userId}/infracciones")
    Call<Void> setInfraccion(@Header("Authorization") String token, @Path("userId") String userId, @Body InfraccionData infraccionData);
}
