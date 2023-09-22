package com.example.mongoserver.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mongoserver.ServerNotFoundException;
import com.example.mongoserver.model.Server;
import com.example.mongoserver.repositories.ServerRepository;

@RestController
@RequestMapping("/servers")
@CrossOrigin
public class ServerController {

    @Autowired
    public ServerRepository serverRepository;

    @GetMapping(value = "/all")
    public List<Server> getAllServers()
    {
        return serverRepository.findAll();

    }

    @GetMapping(value = "/{id}")
    public Server getServerById(@PathVariable String id)
    {
        return serverRepository.findById(id).orElseThrow(() -> new ServerNotFoundException("Server not found with id: " + id));
    }

    @GetMapping("/searchname")
    public List<Server> getServersByName(@RequestParam("name") String name) {
        // Retrieve all servers and filter those whose name contains the provided string
        List<Server> matchingServers = serverRepository.findAll()
                .stream()
                .filter(server -> server.getName().contains(name))
                .collect(Collectors.toList());

        if (matchingServers.isEmpty()) {
            // Return a 404 response if no servers match the criteria
            throw new ServerNotFoundException("No servers found with the provided name");
        }

        return matchingServers;
    }

   
    @PostMapping(value = "/create")
    public String createServer(@RequestBody Server server)
    {
        Server insertedServer = serverRepository.insert(server);
        return "Server created "+insertedServer.getName();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Server> updateServer(@PathVariable String id, @RequestBody Server updatedServer) {
        return serverRepository.findById(id)
                .map(existingServer -> {
                    existingServer.setName(updatedServer.getName());
                    existingServer.setLanguage(updatedServer.getLanguage());
                    existingServer.setFramework(updatedServer.getFramework());
                    serverRepository.save(existingServer);
                    return ResponseEntity.ok(existingServer);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public void DeleteServer(@PathVariable String id)
    {
        serverRepository.deleteById(id);
    }
    
}
