package com.re.service.impl;

import com.re.model.entity.Equipment;
import com.re.repository.EquipmentRepository;
import com.re.service.EquimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquimentServiceImpl implements EquimentService {


    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private BorrowingRecordServiceImpl borrowingRecordService;


    @Override
    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    @Override
    public Page<Equipment> findAll(Pageable pageable){
        return equipmentRepository.findAll(pageable);
    }
    @Override
    public Equipment save(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @Override
    public Equipment findById(Long id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    @Override
    public Equipment update(Equipment equipment) {
        return null;
    }

    @Override
    public void delete(Long id) {

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị để xóa"));

        long activeBorrowCount = equipmentRepository.countActiveBorrowing(id);

        if (activeBorrowCount > 0) {
            throw new RuntimeException("Không thể xóa thiết bị '" + equipment.getName() +
                    "' vì đang có " + activeBorrowCount + " yêu cầu mượn chưa hoàn tất!");
        }

        try {
            equipmentRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi hệ thống: Không thể xóa thiết bị do ràng buộc dữ liệu lịch sử.");
        }
    }
}
