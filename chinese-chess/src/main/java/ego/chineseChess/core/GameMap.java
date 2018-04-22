package ego.chineseChess.core;

import ego.chineseChess.entity.Unit;
import sun.security.provider.MD5;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;

public class GameMap {

    private HashSet<Unit> units;

    private Unit[][] map = new Unit[Config.HEIGHT][Config.WIDTH];

    public GameMap(List<Unit> units) {
        this.units = new HashSet<>(units);
        units.forEach(unit -> map[unit.x][unit.y] = unit);
    }

    public void move(Unit unit, int x, int y) {
        if (map[x][y] != null) {
            units.remove(map[x][y]);
        }
        map[unit.x][unit.y] = null;
        map[x][y] = unit;
        unit.x = x;
        unit.y = y;
    }

    public void undoMove(Unit unit, int x, int y, Unit last) {
        map[unit.x][unit.y] = last;
        if (last != null) {
            units.add(last);
        }
        map[x][y] = unit;
        unit.x = x;
        unit.y = y;
    }

    public HashSet<Unit> getUnits() {
        return new HashSet<>(units);
    }

    public Unit getUnit(int x, int y) {
        return map[x][y];
    }

    public boolean moveAble(int x, int y) {
        if (!inMap(x, y)) {
            return false;
        }
        if (map[x][y] != null) {
            return false;
        }
        return true;
    }

    public boolean attackAble(int x, int y) {
        if (!inMap(x, y)) {
            return false;
        }
        if (map[x][y] != null) {
            return true;
        }
        return false;
    }

    public boolean moveOrAttackAble(int x, int y) {
        if (!inMap(x, y)) {
            return false;
        }
        if (map[x][y] != null) {
            return false;
        }
        return true;
    }

    public boolean inMap(int x, int y) {
        if (x < 0 || x >= Config.HEIGHT)
            return false;
        if (y < 0 || y >= Config.WIDTH)
            return false;
        return true;
    }

    public String getCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < Config.HEIGHT; i++)
            for (int j = 0; j < Config.WIDTH; j++) {
                if (getUnit(i, j) != null) {
                    buffer.append(getUnit(i, j).troop.getLetter());
                } else {
                    buffer.append('.');
                }
            }
        return getMD5(buffer.toString());
    }

    /**
     * 生成md5
     *
     * @param message
     * @return
     */
    private static String getMD5(String message) {
        String md5str = "";
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 2 将消息变成byte数组
            byte[] input = message.getBytes();

            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);

            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder md5str = new StringBuilder();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (byte aByte : bytes) {
            digital = aByte;

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }


}
