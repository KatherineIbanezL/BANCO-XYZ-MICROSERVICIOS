package cl.duoc.core.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job importCuentasJob;
    private final Job importInteresesJob;
    private final Job importTransaccionesJob;

    public BatchController(JobLauncher jobLauncher,
                           @Qualifier("importCuentasJob") Job importCuentasJob,
                           @Qualifier("importInteresesJob") Job importInteresesJob,
                           @Qualifier("importTransaccionesJob") Job importTransaccionesJob) {
        this.jobLauncher = jobLauncher;
        this.importCuentasJob = importCuentasJob;
        this.importInteresesJob = importInteresesJob;
        this.importTransaccionesJob = importTransaccionesJob;
    }

    @PostMapping("/cuentas")
    public ResponseEntity<String> cargarCuentas() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(importCuentasJob, params);
            return ResponseEntity.ok("Job Cuentas ejecutado con estado: " + execution.getStatus());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al ejecutar Job Cuentas: " + e.getMessage());
        }
    }

    @PostMapping("/intereses")
    public ResponseEntity<String> cargarIntereses() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(importInteresesJob, params);
            return ResponseEntity.ok("Job Intereses ejecutado con estado: " + execution.getStatus());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al ejecutar Job Intereses: " + e.getMessage());
        }
    }

    @PostMapping("/transacciones")
    public ResponseEntity<String> cargarTransacciones() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(importTransaccionesJob, params);
            return ResponseEntity.ok("Job Transacciones ejecutado con estado: " + execution.getStatus());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al ejecutar Job Transacciones: " + e.getMessage());
        }
    }
}