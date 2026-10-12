package cl.duoc.clientes.controller;

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
    private final Job importEstadosAnualesJob;
    private final Job importCargaCompletaJob;

    public BatchController(
            JobLauncher jobLauncher, 
            @Qualifier("importEstadosAnualesJob") Job importEstadosAnualesJob,
            @Qualifier("importCargaCompletaJob") Job importCargaCompletaJob) {
        this.jobLauncher = jobLauncher;
        this.importEstadosAnualesJob = importEstadosAnualesJob;
        this.importCargaCompletaJob = importCargaCompletaJob;
    }

    @PostMapping("/ejecutar-estados-anuales")
    public ResponseEntity<String> ejecutarBatchEstadosAnuales(
            @RequestParam(defaultValue = "semana_1") String semana) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("semana", semana)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importEstadosAnualesJob, params);
            return ResponseEntity.ok("Job de Estados Anuales iniciado exitosamente para: " + semana);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error ejecutando Job: " + e.getMessage());
        }
    }

    @PostMapping("/ejecutar-carga-completa")
    public ResponseEntity<String> ejecutarBatchCargaCompleta(
            @RequestParam(defaultValue = "semana_1") String semana) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("semana", semana)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importCargaCompletaJob, params);
            return ResponseEntity.ok("Job Multi-Step de Carga Completa iniciado exitosamente para: " + semana);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error ejecutando Job Multi-Step: " + e.getMessage());
        }
    }
}