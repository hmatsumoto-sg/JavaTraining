package com.s_giken.training.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.s_giken.training.webapp.exception.AttributeErrorException;
import com.s_giken.training.webapp.model.entity.Charge;
import com.s_giken.training.webapp.model.entity.ChargeSearchCondition;
import com.s_giken.training.webapp.repository.IChargeRepository;

/**
 * 料金情報管理機能のサービスクラス(実態クラス)
 */
@Service
public class ChargeService implements IChargeService {
    private IChargeRepository chargeRepository;

    /**
     * 料金情報管理機能のサービスクラスのコンストラクタ
     * 
     * @param chargeRepository 料金情報管理機能のリポジトリクラス(SpringのDIコンテナから渡される)
     */
    public ChargeService(IChargeRepository chargeRepository) {
        this.chargeRepository = chargeRepository;
    }

    /**
     * 料金情報を全件取得する
     * 
     * @return 全加入者情報
     */
    @Override
    public List<Charge> findAll() {
        return chargeRepository.findAll();
    }

    /**
     * 料金情報を1件取得する
     * 
     * @param chargeId 加入者ID
     * @return 加入者IDに一致した加入者情報
     */
    @Override
    public Optional<Charge> findById(Long chargeId) {
        return chargeRepository.findById(chargeId);
    }

    /**
     * 料金情報を条件検索する
     * 
     * @param chargeSearchCondition 加入者検索条件
     * @return 条件に一致した加入者情報
     */
    @Override
    public List<Charge> findByConditions(ChargeSearchCondition chargeSearchCondition) {
        String name = chargeSearchCondition.getName() != null ? "" + chargeSearchCondition.getName() + "" : "%";
        return chargeRepository.findByNameLike(name);
    }

    /**
     * 料金情報を登録する
     *
     * @param charge 登録する料金情報。 chargeIdが Null であること。
     */
    @Override
    public void add(Charge charge) {
        if (charge.getChargeId() != null) {
            throw new AttributeErrorException("料金IDが指定されていると登録できません。");
        }
        chargeRepository.add(charge);
    }

    /**
     * 料金情報を更新する
     * 
     * @param charge 更新する料金情報。chargeId が NULL でないこと
     */
    @Override
    public void update(Charge charge) {
        if (charge.getChargeId() == null) {
            throw new AttributeErrorException("料金IDが指定されていません。");
        }
        chargeRepository.update(charge);
    }

    /**
     * 料金情報を先所する
     * 
     * @param chargeId 料金情報のID
     */
    @Override
    public void deleteById(Long chargeId) {
        chargeRepository.deleteById(chargeId);
    }
}
