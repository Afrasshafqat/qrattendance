package com.app.fyp.qrbasedattendanceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private List<AttendanceRecord> attendanceList;

    public AttendanceAdapter(List<AttendanceRecord> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvRollNo, tvAttendance, tvTimestamp;

        public ViewHolder(View view) {
            super(view);
            tvStudentName = view.findViewById(R.id.tvStudentName);
            tvRollNo = view.findViewById(R.id.tvRollNo);
            tvAttendance = view.findViewById(R.id.tvAttendance);
            tvTimestamp = view.findViewById(R.id.tvTimestamp);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AttendanceRecord record = attendanceList.get(position);
        holder.tvStudentName.setText("Name: " + record.getStudentName());
        holder.tvRollNo.setText("Roll No: " + record.getRollNo());
        holder.tvAttendance.setText("Status: " + record.getAttendance());
        holder.tvTimestamp.setText("Time: " + record.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }
}
