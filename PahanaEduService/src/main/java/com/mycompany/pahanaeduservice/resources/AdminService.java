package com.mycompany.pahanaeduservice.resources;

import com.google.gson.Gson;
import Utils.Admin;
import Utils.AdminDB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AdminService {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String json) {
        Gson gson = new Gson();
        Admin inputAdmin = gson.fromJson(json, Admin.class);

        AdminDB adminDB = new AdminDB();
        Admin admin = adminDB.authenticateAdmin(inputAdmin.getUsername(), inputAdmin.getPassword());

        if (admin != null) {
            // For simplicity, return a success message with admin details
            return Response.status(200).entity(gson.toJson(admin)).build();
        } else {
            return Response.status(401).entity("{\"message\": \"Invalid username or password\"}").build();
        }
    }
}