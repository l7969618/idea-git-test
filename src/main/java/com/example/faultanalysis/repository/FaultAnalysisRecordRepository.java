package com.example.faultanalysis.repository;

import com.example.faultanalysis.model.FaultAnalysisRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaultAnalysisRecordRepository extends JpaRepository<FaultAnalysisRecord, Long> {
}
