-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
-- -----------------------------------------------------
-- Schema QuanLyBanVeOnline
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `QuanLyBanVeOnline` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin ;
USE `QuanLyBanVeOnline` ;

-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`CaLamViec`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`CaLamViec` (
  `idCaLamViec` INT NOT NULL AUTO_INCREMENT,
  `NoiDung` ENUM('CASANG', 'CACHIEU', 'CATOI') NOT NULL,
  `GioBatDau` TIME NOT NULL,
  `GioKetThuc` TIME NOT NULL,
  PRIMARY KEY (`idCaLamViec`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`PhongChieu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`PhongChieu` (
  `idPhongChieu` INT NOT NULL AUTO_INCREMENT,
  `SucChua` INT NOT NULL,
  `TrangThai` ENUM('DANGHOATDONG', 'BAOTRI') NOT NULL,
  PRIMARY KEY (`idPhongChieu`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`Ghe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`Ghe` (
  `idGhe` INT NOT NULL AUTO_INCREMENT,
  `TrangThai` ENUM('TOT', 'BAOTRI') NOT NULL,
  `idPhongChieu` INT NOT NULL,
  `Hang` CHAR(5) NOT NULL,
  `Cot` INT NOT NULL,
  PRIMARY KEY (`idGhe`),
  UNIQUE INDEX `idGhe_UNIQUE` (`idGhe` ASC) VISIBLE,
  UNIQUE INDEX `Vi_Tri_Ghe` (`Cot` ASC, `Hang` ASC, `idPhongChieu` ASC) VISIBLE,
  INDEX `fk_Ghe_PhongChieu1_idx` (`idPhongChieu` ASC) VISIBLE,
  CONSTRAINT `fk_Ghe_PhongChieu1`
    FOREIGN KEY (`idPhongChieu`)
    REFERENCES `QuanLyBanVeOnline`.`PhongChieu` (`idPhongChieu`))
ENGINE = InnoDB
AUTO_INCREMENT = 81
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`NguoiDung`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`NguoiDung` (
  `idNguoiDung` CHAR(20) NOT NULL,
  `TaiKhoan` CHAR(100) NOT NULL,
  `MatKhau` CHAR(255) NOT NULL,
  `HoTen` CHAR(100) NOT NULL,
  `NgaySinh` DATE NOT NULL,
  `SDT` CHAR(20) NOT NULL,
  `Email` CHAR(100) NOT NULL,
  `VaiTro` ENUM('KHACHHANG', 'NHANVIEN', 'QUANLY') NOT NULL,
  `NgayBatDau` DATE NULL DEFAULT NULL,
  `TrangThai` ENUM('DANGLAM', 'NGHIPHEP') NULL DEFAULT NULL,
  `idQuanLy` CHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`idNguoiDung`),
  UNIQUE INDEX `TaiKhoan_UNIQUE` (`TaiKhoan` ASC) VISIBLE,
  UNIQUE INDEX `Email_UNIQUE` (`Email` ASC) VISIBLE,
  UNIQUE INDEX `SDT_UNIQUE` (`SDT` ASC) VISIBLE,
  INDEX `fk_NguoiDung_NguoiQuanLy_idx` (`idQuanLy` ASC) VISIBLE,
  CONSTRAINT `fk_NguoiDung_NguoiQuanLy`
    FOREIGN KEY (`idQuanLy`)
    REFERENCES `QuanLyBanVeOnline`.`NguoiDung` (`idNguoiDung`)
    ON DELETE SET NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`PhuongThucThanhToan`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`PhuongThucThanhToan` (
  `idPhuongThucThanhToan` INT NOT NULL AUTO_INCREMENT,
  `NoiDung` CHAR(45) NOT NULL,
  PRIMARY KEY (`idPhuongThucThanhToan`),
  UNIQUE INDEX `NoiDung_UNIQUE` (`NoiDung` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`HoaDon`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`HoaDon` (
  `idHoaDon` INT NOT NULL AUTO_INCREMENT,
  `NgayThanhToan` DATETIME NULL DEFAULT NULL,
  `TrangThai` ENUM('DATHANHTOAN', 'DAHUY') NOT NULL,
  `idPhuongThucThanhToan` INT NOT NULL,
  `idNguoiDung` CHAR(20) NOT NULL,
  PRIMARY KEY (`idHoaDon`),
  INDEX `fk_HoaDon_PhuongThucThanhToan1_idx` (`idPhuongThucThanhToan` ASC) VISIBLE,
  INDEX `fk_HoaDon_NguoiDung1_idx` (`idNguoiDung` ASC) VISIBLE,
  CONSTRAINT `fk_HoaDon_NguoiDung1`
    FOREIGN KEY (`idNguoiDung`)
    REFERENCES `QuanLyBanVeOnline`.`NguoiDung` (`idNguoiDung`),
  CONSTRAINT `fk_HoaDon_PhuongThucThanhToan1`
    FOREIGN KEY (`idPhuongThucThanhToan`)
    REFERENCES `QuanLyBanVeOnline`.`PhuongThucThanhToan` (`idPhuongThucThanhToan`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`LichCuThe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`LichCuThe` (
  `idLichCuThe` INT NOT NULL AUTO_INCREMENT,
  `NgayLamViec` DATE NOT NULL,
  `idCaLamViec` INT NOT NULL,
  `idNguoiDung` CHAR(20) NOT NULL,
  PRIMARY KEY (`idLichCuThe`),
  UNIQUE INDEX `idLichCuThe_UNIQUE` (`idLichCuThe` ASC) VISIBLE,
  UNIQUE INDEX `PhanCong_UNIQUE` (`NgayLamViec` ASC, `idCaLamViec` ASC, `idNguoiDung` ASC) VISIBLE,
  INDEX `fk_LichCuThe_CaLamViec1_idx` (`idCaLamViec` ASC) VISIBLE,
  INDEX `fk_LichCuThe_NguoiDung1_idx` (`idNguoiDung` ASC) VISIBLE,
  CONSTRAINT `fk_LichCuThe_CaLamViec1`
    FOREIGN KEY (`idCaLamViec`)
    REFERENCES `QuanLyBanVeOnline`.`CaLamViec` (`idCaLamViec`),
  CONSTRAINT `fk_LichCuThe_NguoiDung1`
    FOREIGN KEY (`idNguoiDung`)
    REFERENCES `QuanLyBanVeOnline`.`NguoiDung` (`idNguoiDung`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`Phim`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`Phim` (
  `idPhim` INT NOT NULL AUTO_INCREMENT,
  `Ten` CHAR(100) NOT NULL,
  `NgayPhatHanh` DATE NOT NULL,
  `ThoiLuong` FLOAT NOT NULL,
  `NgonNguChinh` CHAR(50) NOT NULL,
  `NoiDung` TEXT NOT NULL,
  `idNguoiDung` CHAR(20) NOT NULL,
  PRIMARY KEY (`idPhim`),
  INDEX `fk_Phim_NguoiDung1_idx` (`idNguoiDung` ASC) VISIBLE,
  CONSTRAINT `fk_Phim_NguoiDung1`
    FOREIGN KEY (`idNguoiDung`)
    REFERENCES `QuanLyBanVeOnline`.`NguoiDung` (`idNguoiDung`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`SuatChieu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`SuatChieu` (
  `idSuatChieu` INT NOT NULL AUTO_INCREMENT,
  `GiaVe` INT NOT NULL,
  `ThoiGianBatDau` DATETIME NOT NULL,
  `idPhongChieu` INT NOT NULL,
  `idPhim` INT NOT NULL,
  `idNguoiDung` CHAR(20) NOT NULL,
  PRIMARY KEY (`idSuatChieu`),
  UNIQUE INDEX `TranhTrungGio_UNIQUE` (`idPhongChieu` ASC, `ThoiGianBatDau` ASC) VISIBLE,
  INDEX `fk_SuatChieu_PhongChieu1_idx` (`idPhongChieu` ASC) VISIBLE,
  INDEX `fk_SuatChieu_Phim1_idx` (`idPhim` ASC) VISIBLE,
  INDEX `fk_SuatChieu_NguoiDung1_idx` (`idNguoiDung` ASC) VISIBLE,
  CONSTRAINT `fk_SuatChieu_NguoiDung1`
    FOREIGN KEY (`idNguoiDung`)
    REFERENCES `QuanLyBanVeOnline`.`NguoiDung` (`idNguoiDung`),
  CONSTRAINT `fk_SuatChieu_Phim1`
    FOREIGN KEY (`idPhim`)
    REFERENCES `QuanLyBanVeOnline`.`Phim` (`idPhim`),
  CONSTRAINT `fk_SuatChieu_PhongChieu1`
    FOREIGN KEY (`idPhongChieu`)
    REFERENCES `QuanLyBanVeOnline`.`PhongChieu` (`idPhongChieu`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`TheLoai`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`TheLoai` (
  `idTheLoai` INT NOT NULL AUTO_INCREMENT,
  `NoiDung` CHAR(45) NOT NULL,
  PRIMARY KEY (`idTheLoai`),
  UNIQUE INDEX `NoiDung_UNIQUE` (`NoiDung` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`TheLoaiPhim`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`TheLoaiPhim` (
  `idTheLoai` INT NOT NULL,
  `idPhim` INT NOT NULL,
  PRIMARY KEY (`idTheLoai`, `idPhim`),
  INDEX `fk_TheLoai_has_Phim_Phim1_idx` (`idPhim` ASC) VISIBLE,
  INDEX `fk_TheLoai_has_Phim_TheLoai1_idx` (`idTheLoai` ASC) VISIBLE,
  CONSTRAINT `fk_TheLoai_has_Phim_Phim1`
    FOREIGN KEY (`idPhim`)
    REFERENCES `QuanLyBanVeOnline`.`Phim` (`idPhim`),
  CONSTRAINT `fk_TheLoai_has_Phim_TheLoai1`
    FOREIGN KEY (`idTheLoai`)
    REFERENCES `QuanLyBanVeOnline`.`TheLoai` (`idTheLoai`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `QuanLyBanVeOnline`.`VeXemPhim`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `QuanLyBanVeOnline`.`VeXemPhim` (
  `idVeXemPhim` INT NOT NULL AUTO_INCREMENT,
  `TrangThai` ENUM('CHUASUDUNG', 'DASUDUNG', 'HETHAN') NOT NULL,
  `idSuatChieu` INT NOT NULL,
  `idHoaDon` INT NOT NULL,
  `idGhe` INT NOT NULL,
  PRIMARY KEY (`idVeXemPhim`),
  UNIQUE INDEX `TrungVe_UNIQUE` (`idSuatChieu` ASC, `idGhe` ASC) INVISIBLE,
  INDEX `fk_VeXemPhim_SuatChieu1_idx` (`idSuatChieu` ASC) VISIBLE,
  INDEX `fk_VeXemPhim_HoaDon1_idx` (`idHoaDon` ASC) VISIBLE,
  INDEX `fk_VeXemPhim_Ghe1_idx` (`idGhe` ASC) VISIBLE,
  CONSTRAINT `fk_VeXemPhim_Ghe1`
    FOREIGN KEY (`idGhe`)
    REFERENCES `QuanLyBanVeOnline`.`Ghe` (`idGhe`),
  CONSTRAINT `fk_VeXemPhim_HoaDon1`
    FOREIGN KEY (`idHoaDon`)
    REFERENCES `QuanLyBanVeOnline`.`HoaDon` (`idHoaDon`),
  CONSTRAINT `fk_VeXemPhim_SuatChieu1`
    FOREIGN KEY (`idSuatChieu`)
    REFERENCES `QuanLyBanVeOnline`.`SuatChieu` (`idSuatChieu`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;