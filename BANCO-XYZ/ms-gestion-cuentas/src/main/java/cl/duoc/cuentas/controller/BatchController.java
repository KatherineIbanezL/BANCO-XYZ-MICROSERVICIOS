package cl.duoc.cuentas.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job importInteresesJob;
    private final Job importCargaCompletaJob;

    public BatchController(
            JobLauncher jobLauncher, 
            @Qualifier("importInteresesJob") Job importInteresesJob,
            @Qualifier("importCargaCompletaJob") Job importCargaCompletaJob) {
        this.jobLauncher = jobLauncher;
        this.importInteresesJob = importInteresesJob;
        this.importCargaCompletaJob = importCargaCompletaJob;
    }

    // 1. Endpoint original adaptado para aceptar la semana dinámicamente
    @PostMapping("/ejecutar-intereses")
    public ResponseEntity<String> ejecutarBatchIntereses(
            @RequestParam(defaultValue = "semana_1") String semana) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("semana", semana)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importInteresesJob, params);
            return ResponseEntity.ok("Job de Intereses iniciado exitosamente para: " + semana);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error ejecutando Job de Intereses: " + e.getMessage());
        }
    }

    // 2. Nuevo endpoint para la Carga Completa Multi-Step (Estados -> Intereses -> Movimientos)
    @PostMapping("/ejecutar-carga-completa")
    public ResponseEntity<String> ejecutarBatchCargaCompleta(
            @RequestParam(defaultValue = "semana_1") String semana) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("semana", semana)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importCargaCompletaJob, params);
            return ResponseEntity.ok("Job de Carga Completa Multi-Step iniciado exitosamente para: " + semana);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error ejecutando Job de Carga Completa: " + e.getMessage());
        }
    }
}