package com.s_giken.training.webapp.repository;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.s_giken.training.webapp.model.entity.Charge;

@Repository
public class ChargeRepository implements IChargeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Charge> rowMapper;

    public ChargeRepository(JdbcTemplate jdbcTemplate, RowMapper<Charge> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    /**
     * 料金情報をすべて取得する。
     *
     * @return Chargeオブジェクトのリスト
     */
    @Override
    public List<Charge> findAll() {
        String sql = "SELECT * FROM T_CHARGE";
        List<Charge> result = jdbcTemplate.query(sql, rowMapper);
        return result;
    }

    /**
     * 料金IDの一部に一致する料金情報リストを取得する。
     *
     * @return Optional型の Chargeオブジェクト
     */
    @Override
    public Optional<Charge> findById(Long id) {
        String sql = "SELECT * FROM T_CHARGE WHERE charge_id = ?";
        Object[] args = { id };
        int[] argTypes = { Types.BIGINT };

        Charge charge;
        try {
            charge = jdbcTemplate.queryForObject(sql, args, argTypes, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            charge = null;
        }

        return Optional.ofNullable(charge);
    }

    /**
     * 料金名と部分一致する料金情報リストを取得する。
     *
     * @return Optional型の Chargeオブジェクト
     */
    @Override
    public List<Charge> findByNameLike(String name) {
        String sql = "SELECT * FROM T_CHARGE WHERE name like ? ";
        Object[] args = { "%" + (name != null ? name : "") + "%"};
        int[] argTypes = { Types.VARCHAR };
        List<Charge> result = jdbcTemplate.query(sql, args, argTypes, rowMapper);
        return result;
    }

    /**
     * 料金情報をデータベースへ登録する。
     *
     * @param charge 追加するChargeオブジェクト。 chargeIdプロパティの値は null としなくてはならない
     * @return 処理した件数
     */
    @Override
    public int add(Charge charge) {
        Long chargeId = charge.getChargeId();
        if (chargeId == null) {
            chargeId = jdbcTemplate.queryForObject("SELECT nextval('t_charge_seq')", Long.class);
            charge.setChargeId(chargeId);
        }
        

        String sql = """
                    INSERT INTO T_CHARGE (
                        charge_id,
                        name,
                        amount,
                        start_date,
                        end_date)
                    VALUES (
                        ?,
                        ?,
                        ?,
                        ?,
                        ?)
                """;
        int processed_count = jdbcTemplate.update(
                sql,
                chargeId,
                charge.getName(),
                charge.getAmount(),
                charge.getStartDate(),
                charge.getEndDate());

        return processed_count;
    }

    /**
     * データベースの料金情報を更新する。
     *
     * @param charge 更新するChargeオブジェクト。 chargeIdプロパティには値が設定されている必要がある。
     * @return 処理した件数
     */
    @Override
    public int update(Charge charge) {
        String sql = """
                    UPDATE T_CHARGE
                    SET name = ?,
                        amount = ?,
                        start_date = ?,
                        end_date = ?
                    WHERE charge_id = ?
                """;
        int processed_count = jdbcTemplate.update(
                sql,
                charge.getName(),
                charge.getAmount(),
                charge.getStartDate(),
                charge.getEndDate(),
                charge.getChargeId());

        return processed_count;
    }

    /**
     * データベースから指定した料金IDの料金情報を削除する。
     *
     * @param id 料金ID
     * @return 処理した件数
     */
    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM T_CHARGE WHERE charge_id = ?";

        int processed_count = jdbcTemplate.update(sql, id);

        return processed_count;
    }
}
