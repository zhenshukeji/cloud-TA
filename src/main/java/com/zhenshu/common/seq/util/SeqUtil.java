package com.zhenshu.common.seq.util;

import com.zhenshu.common.seq.exception.SequenceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Random;

public class SeqUtil {
    private static final Log log = LogFactory.getLog(SeqUtil.class);

    public SeqUtil() {
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException var2) {
                log.debug("Could not close JDBC Statement", var2);
            } catch (Throwable var3) {
                log.debug("Unexpected exception on closing JDBC Statement", var3);
            }
        }

    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException var2) {
                log.debug("Could not close JDBC Connection", var2);
            } catch (Throwable var3) {
                log.debug("Unexpected exception on closing JDBC Connection", var3);
            }
        }

    }

    public static String getSeqInsertSql(String tableName, String nameColumnName, String valueColumnName, String gmtCreate, String gmtModified) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("insert into ").append(tableName).append("(");
        buffer.append(nameColumnName).append(",");
        buffer.append(valueColumnName).append(",");
        buffer.append(gmtModified).append(",");
        buffer.append(gmtCreate).append(") values(?,?,?,?);");
        return buffer.toString();
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException var2) {
                log.debug("Could not close JDBC ResultSet", var2);
            } catch (Throwable var3) {
                log.debug("Unexpected exception on closing JDBC ResultSet", var3);
            }
        }

    }

    public static String getSeqSelectSql(String tableName, String nameColumnName, String valueColumnName) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("select ").append(valueColumnName);
        buffer.append(" from ").append(tableName);
        buffer.append(" where ").append(nameColumnName).append(" = ?");
        return buffer.toString();
    }

    public static String getSeqUpdateSql(String tableName, String nameColumnName, String valueColumnName, String gmtModified) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("update ").append(tableName);
        buffer.append(" set ").append(valueColumnName).append(" = ? ,");
        buffer.append(gmtModified).append(" = ? ");
        buffer.append(" where ");
        buffer.append(nameColumnName).append(" = ? and ");
        buffer.append(valueColumnName).append(" = ? ");
        return buffer.toString();
    }

    public static int updateSeqValue(DataSource dataSource, String updateSql, long newValue, String seqName, long oldValue) {
        Connection conn = null;
        PreparedStatement stmt = null;
        Object rs = null;

        int var11;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(updateSql);
            stmt.setLong(1, newValue);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, seqName);
            stmt.setLong(4, oldValue);
            int affectedRows = stmt.executeUpdate();
            var11 = affectedRows;
        } catch (SQLException var15) {
            throw new SequenceException(var15);
        } finally {
            closeResultSet((ResultSet)rs);
            closeStatement(stmt);
            closeConnection(conn);
        }

        return var11;
    }

    public static Long selectSeqValue(DataSource dataSource, String selectSql, String seqName) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Long var7;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(selectSql);
            stmt.setString(1, seqName);
            rs = stmt.executeQuery();
            boolean hasNext = rs.next();
            if (hasNext) {
                var7 = rs.getLong(1);
                return var7;
            }

            var7 = null;
        } catch (SQLException var11) {
            throw new SequenceException(var11);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
            closeConnection(conn);
        }

        return var7;
    }

    public static int insertSeq(DataSource dataSource, String insertSql, String seqName, long startValue) throws SequenceException {
        Connection conn = null;
        PreparedStatement stmt = null;
        Object rs = null;

        int var10;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, seqName);
            stmt.setLong(2, startValue);
            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(3, timeStamp);
            stmt.setTimestamp(4, timeStamp);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SequenceException("faild to auto adjust init value at  " + seqName + " update affectedRow =0");
            }

            if (log.isDebugEnabled()) {
                log.debug("插入初值:" + seqName + ", value:" + startValue);
            }

            var10 = affectedRows;
        } catch (SQLException var14) {
            log.error("由于SQLException,插入初值自适应失败！dbGroupIndex:，sequence Name：" + seqName + "   value:" + startValue, var14);
            throw new SequenceException("由于SQLException,插入初值自适应失败！dbGroupIndex:，sequence Name：" + seqName + "   value:" + startValue, var14);
        } finally {
            closeResultSet((ResultSet)rs);
            closeStatement(stmt);
            closeConnection(conn);
        }

        return var10;
    }

    public static int[] randomIntSequence(int n) throws SequenceException {
        if (n <= 0) {
            throw new SequenceException("产生随机序列范围值小于等于0");
        } else {
            int[] num = new int[n];

            for(int i = 0; i < n; num[i] = i++) {
            }

            if (n == 1) {
                return num;
            } else {
                Random random = new Random();
                int i;
                if (n == 2 && random.nextInt(2) == 1) {
                    i = num[0];
                    num[0] = num[1];
                    num[1] = i;
                }

                for(i = 0; i < n + 10; ++i) {
                    int rindex = random.nextInt(n);
                    int mindex = random.nextInt(n);
                    int temp = num[mindex];
                    num[mindex] = num[rindex];
                    num[rindex] = temp;
                }

                return num;
            }
        }
    }
}
