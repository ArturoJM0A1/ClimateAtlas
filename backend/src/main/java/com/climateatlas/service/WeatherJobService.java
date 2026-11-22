package com.climateatlas.service;

import com.climateatlas.dto.JobExecutionResponse;
import com.climateatlas.exception.WeatherProviderException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Arranca el mismo Job tanto desde HTTP como desde el scheduler. */
@Service
public class WeatherJobService {

    private final JobLauncher jobLauncher;
    private final Job weatherRefreshJob;

    public WeatherJobService(JobLauncher jobLauncher,
                             @Qualifier("weatherRefreshJob") Job weatherRefreshJob) {
        this.jobLauncher = jobLauncher;
        this.weatherRefreshJob = weatherRefreshJob;
    }

    public JobExecutionResponse start() {
        // Un parámetro identificador distinto crea una instancia nueva del Job.
        JobParameters parameters = new JobParametersBuilder()
                .addLong("requestedAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            JobExecution execution = jobLauncher.run(weatherRefreshJob, parameters);
            return JobExecutionResponse.from(execution);
        } catch (Exception exception) {
            throw new WeatherProviderException(
                    "No fue posible iniciar el proceso de actualización meteorológica.", exception);
        }
    }
}
