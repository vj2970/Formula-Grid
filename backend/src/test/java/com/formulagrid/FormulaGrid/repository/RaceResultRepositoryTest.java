package com.formulagrid.FormulaGrid.repository;

import com.formulagrid.FormulaGrid.model.Circuit;
import com.formulagrid.FormulaGrid.model.Constructor;
import com.formulagrid.FormulaGrid.model.Driver;
import com.formulagrid.FormulaGrid.model.RaceResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RaceResultRepositoryTest {

    @Autowired
    private RaceResultRepository raceResultRepository;

    @Test
    public void testSaveRaceResult(){
        Driver driver = new Driver();
        driver.setDriverId("test_driver");
        driver.setGivenName("Test");
        driver.setFamilyName("Driver");

        Constructor constructor = new Constructor();
        constructor.setConstructorId("test_team");
        constructor.setName("Test Team");

        Circuit circuit = new Circuit();
        circuit.setCircuitId("test_circuit");
        circuit.setCircuitName("Test Circuit");

        RaceResult result = new RaceResult();
        result.setSeason(2024);
        result.setRound(1);
        result.setRaceName("Test Grand Prix");
        result.setPosition(1);
        result.setPoints(25);
        result.setDriver(driver);
        result.setConstructor(constructor);
        result.setCircuit(circuit);

        // Save
        RaceResult saved = raceResultRepository.save(result);

        // Verify
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPosition()).isEqualTo(1);

        // Cleanup
        raceResultRepository.delete(saved);

    }

}
