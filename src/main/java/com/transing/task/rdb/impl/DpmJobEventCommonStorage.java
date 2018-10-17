package com.transing.task.rdb.impl;

import com.jeeframework.jeetask.event.rdb.impl.JobEventCommonStorageProcessor;
import com.jeeframework.jeetask.event.type.JobExecutionEvent;
import com.jeeframework.util.format.DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;

/**
 * Created by Administrator on 2017/9/7.
 */
public class DpmJobEventCommonStorage extends JobEventCommonStorageProcessor {

    private static final Logger log = LoggerFactory.getLogger(DpmJobEventCommonStorage.class);

    @Override
    protected void createTaskTable(Connection conn) throws SQLException {
        String dbSchema = "CREATE TABLE `"+tableName+"` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `job_name` varchar(100) NOT NULL,\n" +
                "  `state` varchar(20) NOT NULL,\n" +
                "  `progress` int(11) NOT NULL,\n" +
                "  `ip` varchar(20) NOT NULL,\n" +
                "  `param` text, \n" +
                "  `message` varchar(500) NOT NULL,\n" +
                "  `create_time` timestamp NULL DEFAULT NULL,\n" +
                "  `start_time` timestamp NULL DEFAULT NULL,\n" +
                "  `complete_time` timestamp NULL DEFAULT NULL,\n" +
                "  `failure_cause` varchar(4000) DEFAULT NULL,\n" +
                "  `project_id` int(11) DEFAULT NULL,\n" +
                "  `flow_id` int(11) DEFAULT NULL,\n" +
                "  `flow_detail_id` int(11) DEFAULT NULL,\n" +
                "  `type_no` varchar(64) DEFAULT NULL,\n" +
                "  `job_progress` varchar(32) DEFAULT NULL,\n" +
                "  `error_message` text COMMENT '错误描述',\n" +
                "  `result_jsonParam` text COMMENT '返回jsonParam对象',\n" +
                "  `jsonParam` text,\n" +
                "  `param_type` int(11) DEFAULT '0',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `idx_task_id_state` (`id`,`state`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(dbSchema)) {
            preparedStatement.execute();
        }

    }

    public DpmJobEventCommonStorage(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean insertJobExecutionEvent(JobExecutionEvent jobExecutionEvent) {

        boolean result = false;
        String sql = "INSERT INTO `"+tableName+"` (\n" +
                "\t`job_name`,\n" +
                "\t`state`,\n" +
                "\t`progress`,\n" +
                "\t`ip`,\n" +
                "\t`param`,\n" +
                "\t`message`,\n" +
                "\t`create_time`,\n" +
                "\t`start_time`,\n" +
                "\t`complete_time`,\n" +
                "\t`project_id`,\n" +
                "\t`flow_id`,\n" +
                "\t`flow_detail_id`,\n" +
                "\t`type_no`,\n" +
                "\t`job_progress`,\n" +
                "\t`error_message`,\n" +
                "\t`result_jsonParam`,\n" +
                "\t`jsonParam`,\n" +
                "\t`param_type`\n" +
                ")\n" +
                "VALUES\n" +
                "\t(\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?,\n" +
                "\t\t?\n" +
                "\t);\n" +
                "\n";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DpmTask dpmTask = (DpmTask) jobExecutionEvent.getTask();
            preparedStatement.setString(1, dpmTask.getName());
            preparedStatement.setString(2, jobExecutionEvent.getState().toString());
            preparedStatement.setInt(3, 0);
            preparedStatement.setString(4, jobExecutionEvent.getIp());
            preparedStatement.setString(5, dpmTask.getParam());
            preparedStatement.setString(6, "");
            preparedStatement.setTimestamp(7, new Timestamp(jobExecutionEvent.getCreateTime().getTime()));
            try {
                preparedStatement.setTimestamp(8, new Timestamp(DateFormat.parseDate("1971-01-01 00:00:00", DateFormat
                        .DT_YYYY_MM_DD_HHMMSS).getTime()));
            } catch (ParseException e) {
            }
            try {
                preparedStatement.setTimestamp(9, new Timestamp(DateFormat.parseDate("1971-01-01 00:00:00", DateFormat
                        .DT_YYYY_MM_DD_HHMMSS).getTime()));
            } catch (ParseException e) {
            }

            preparedStatement.setLong(10,dpmTask.getProjectId());
            preparedStatement.setLong(11,dpmTask.getFlowId());
            preparedStatement.setLong(12,dpmTask.getFlowDetailId());
            preparedStatement.setString(13,dpmTask.getTypeNo());
            preparedStatement.setString(14,dpmTask.getJobProgress());
            preparedStatement.setString(15,dpmTask.getErrorMessage());
            preparedStatement.setString(16,dpmTask.getResultJsonParam());
            preparedStatement.setString(17,dpmTask.getJsonParam());
            preparedStatement.setInt(18,dpmTask.getParamType());

            preparedStatement.execute();
            ResultSet rs = preparedStatement.getGeneratedKeys();

            Object retId = null;
            if (rs.next())
                retId = rs.getObject(1);
            else
                throw new SQLException("insert or generate keys failed..");

            jobExecutionEvent.setTaskId(Long.valueOf(retId + ""));
            result = true;
        } catch (final SQLException ex) {
            if (!isDuplicateRecord(ex)) {
                // TODO 记录失败直接输出日志,未来可考虑配置化
                log.error(ex.getMessage());
            }
        }
        return result;

    }
}
