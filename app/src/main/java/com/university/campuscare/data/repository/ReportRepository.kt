package com.university.campuscare.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.university.campuscare.data.model.Issue
import com.university.campuscare.utils.DataResult
import com.university.campuscare.utils.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface ReportRepository {
    fun createReport(report: Issue): Flow<DataResult<Unit>>
    fun getAllReports(): Flow<DataResult<List<Issue>>>
    fun getReportsByUser(userId: String): Flow<DataResult<List<Issue>>>
    fun getReportById(reportId: String): Flow<DataResult<Issue>>
    fun updateReportStatus(reportId: String, newStatus: String): Flow<DataResult<Unit>>
    fun deleteReport(reportId: String): Flow<DataResult<Unit>>
}

class ReportRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ReportRepository {

    private val reportsCollection = firestore.collection("reports")

    override fun createReport(report: Issue): Flow<DataResult<Unit>> = flow {
        emit(DataResult.Loading)
        try {
            val docRef = if (report.id.isEmpty()) {
                reportsCollection.document()
            } else {
                reportsCollection.document(report.id)
            }
            
            val reportToSave = report.copy(id = docRef.id)
            
            docRef.set(reportToSave).await()
            emit(DataResult.Success(Unit))
        } catch (e: Exception) {
            emit(DataResult.Error(Event(e.message ?: "Failed to create report")))
        }
    }

    override fun getAllReports(): Flow<DataResult<List<Issue>>> = flow {
        emit(DataResult.Loading)
        try {
            val snapshot = reportsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val reports = snapshot.toObjects(Issue::class.java)
            emit(DataResult.Success(reports))
        } catch (e: Exception) {
            emit(DataResult.Error(Event(e.message ?: "Failed to fetch reports")))
        }
    }

    override fun getReportsByUser(userId: String): Flow<DataResult<List<Issue>>> = flow {
        emit(DataResult.Loading)
        try {
            val snapshot = reportsCollection
                .whereEqualTo("reportedBy", userId)
                .get()
                .await()
            
            val reports = snapshot.toObjects(Issue::class.java)
            emit(DataResult.Success(reports))
        } catch (e: Exception) {
            emit(DataResult.Error(Event(e.message ?: "Failed to fetch user reports")))
        }
    }

    override fun getReportById(reportId: String): Flow<DataResult<Issue>> = flow {
        emit(DataResult.Loading)
        try {
            val doc = reportsCollection.document(reportId).get().await()
            val report = doc.toObject(Issue::class.java)
            if (report != null) {
                emit(DataResult.Success(report))
            } else {
                emit(DataResult.Error(Event("Report not found")))
            }
        } catch (e: Exception) {
            emit(DataResult.Error(Event(e.message ?: "Failed to fetch report details")))
        }
    }

    override fun updateReportStatus(reportId: String, newStatus: String): Flow<DataResult<Unit>> = flow {
        emit(DataResult.Loading)
        try {
            reportsCollection.document(reportId)
                .update("status", newStatus, "updatedAt", System.currentTimeMillis())
                .await()
            emit(DataResult.Success(Unit))
        } catch (e: Exception) {
            emit(DataResult.Error(Event(e.message ?: "Failed to update report status")))
        }
    }

    override fun deleteReport(reportId: String): Flow<DataResult<Unit>> = flow {
        emit(DataResult.Loading)
        try {
            reportsCollection.document(reportId).delete().await()
            emit(DataResult.Success(Unit))
        } catch (e: Exception) {
            emit(DataResult.Error(Event(e.message ?: "Failed to delete report")))
        }
    }
}
