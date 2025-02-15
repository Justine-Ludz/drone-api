package com.medication.drone.service.scheduler;

import com.medication.drone.constants.LoadStatus;
import com.medication.drone.repository.entity.Drone;
import com.medication.drone.repository.entity.LoadHistory;
import com.medication.drone.repository.entity.Medicine;
import com.medication.drone.repository.DroneRepository;
import com.medication.drone.repository.LoadHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.medication.drone.constants.DroneState.LOADING;
import static com.medication.drone.constants.DroneState.LOADED;
import static com.medication.drone.constants.DroneState.DELIVERED;
import static com.medication.drone.constants.DroneState.DELIVERING;
import static com.medication.drone.constants.DroneState.IDLE;
import static com.medication.drone.constants.DroneState.RETURNING;

@Slf4j
@Service
public class DroneServiceScheduler {

    @Autowired private DroneRepository droneRepository;

    @Autowired private LoadHistoryRepository loadHistoryRepository;

    @Transactional
    @Scheduled(fixedDelay = 15000)
    public void droneProcessScheduler(){
        List<Drone> drones = new ArrayList<>();
        log.info("Refreshing...");
        try{
            drones = (List<Drone>) droneRepository.findAll();
        } catch(Exception e) {
            log.error(e.toString());
        }

        log.info("processing {} drone infos...", (long) drones.size());
        try{
            for(Drone drone : drones) {
                switch (drone.getState()) {
                    case IDLE:
                        Double battery = drone.getBatteryCapacity();
                        //it takes 25% of the battery when it delivers things
                        if(isNearCapacity(drone) && (battery >= 25.0)){
                            drone.setState(LOADING);
                        }
                        else{
                            //when idle, is charging
                            if(battery < 100.0){
                                battery += 5.0;
                                drone.setBatteryCapacity(battery);
                            }
                        }
                        break;
                    case LOADING:
                        if(Objects.nonNull(drone.getMedicine())) {
                            drone.setState(LOADED);
                        }
                        break;
                    case LOADED:
                        drone.setState(DELIVERING);
                        break;
                    case DELIVERING:
                        drone.setState(DELIVERED);
                        break;
                    case DELIVERED:
                        drone.setState(RETURNING);
                        saveToHistory(drone.getMedicine());
                        break;
                    case RETURNING:
                        drone.setBatteryCapacity(drone.getBatteryCapacity() - 25.0);
                        drone.getMedicine().clear();
                        drone.setState(IDLE);
                        break;
                    default:
                        log.info("error");
                }
                droneRepository.save(drone);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }

    }

    //if drone is 90% or more of max capacity, meds can now be loaded to drone
    private boolean isNearCapacity(Drone drone) {
        double totalLoad = 0;
        for(Medicine meds : drone.getMedicine()) {
            totalLoad += meds.getWeight();
        }
        //90% of total
        double nearCapacity = .90 * drone.getWeightLimit();

        return  totalLoad >= nearCapacity;
    }

    private void saveToHistory(List<Medicine> medicine) {
        for(Medicine meds : medicine){
            LoadHistory loadHistory = LoadHistory.builder()
                    .name(meds.getName())
                    .code(meds.getCode())
                    .weight(meds.getWeight())
                    .status(LoadStatus.DELIVERED)
                    .date(LocalDateTime.now())
                    .build();
            try{
                loadHistoryRepository.save(loadHistory);
            }
            catch (Exception e){
                log.error(e.toString());
            }
        }
    }
}
