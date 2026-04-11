package com.example.demo.routes;

import com.example.demo.models.PropertyModel;
import com.example.demo.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class PropertyRoute {

    @Autowired
    private PropertyRepository propertyRepository;

    @PostMapping("/api/property")
    public PropertyModel addProperty(@RequestBody PropertyModel property) {
        return propertyRepository.save(property);
    }

    // all properties
    @GetMapping("/api/property")
    public Iterable<PropertyModel> getAllProperties() {
        return propertyRepository.findAll();
    }

    // property by id
    @GetMapping("/api/property/{id}")
    public PropertyModel getProperty(@PathVariable Long id) {
        return propertyRepository.findById(id).orElse(null);
    }
    
    // update property by id
    @PutMapping("/api/property/{id}")
    public PropertyModel updateProperty(@PathVariable Long id, @RequestBody PropertyModel property) {
        property.setId(id);
        return propertyRepository.save(property);
    }

    // delete property by id
    @DeleteMapping("/api/property/{id}")
    public void deleteProperty(@PathVariable Long id) {
        propertyRepository.deleteById(id);
    }
    
}
