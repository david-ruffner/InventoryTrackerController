package com.davidruffner.inventorytrackercontroller;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
        glue = "com.davidruffner.inventorytrackercontroller.steps"
)
public class InventoryControllerIT {

}
