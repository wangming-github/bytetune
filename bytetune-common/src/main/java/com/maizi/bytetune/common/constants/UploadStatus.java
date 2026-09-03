package com.maizi.bytetune.common.constants;

/**
 * 文件上传状态枚举
 *
 * <p>
 * 用于标识歌曲文件在 MinIO 中的上传生命周期状态。
 * 数据库存储的是 code，而不是枚举 ordinal，避免顺序变更导致数据错乱。
 * </p>
 *
 * <p>状态流转：</p>
 * <pre>
 * NOT_UPLOADED → UPLOADING → SUCCESS
 *                        ↘ FAILED
 * </pre>
 *
 * <p>数据库字段：status TINYINT</p>
 */
public enum UploadStatus {

    /**
     * 未上传
     * 初始状态，数据库刚创建记录时默认值
     */
    NOT_UPLOADED(0, "未上传"),

    /**
     * 上传中
     * 文件正在传输到 MinIO
     */
    UPLOADING(1, "上传中"),

    /**
     * 上传成功
     * 文件已成功存储到 MinIO
     */
    SUCCESS(2, "上传成功"),

    /**
     * 上传失败
     * 上传过程中发生异常
     */
    FAILED(3, "上传失败");

    /**
     * 数据库存储值
     */
    private final int code;

    /**
     * 状态描述（用于前端展示或日志）
     */
    private final String desc;

    /**
     * 构造方法
     *
     * @param code 数据库存储值
     * @param desc 状态描述
     */
    UploadStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取数据库存储值
     *
     * @return 状态 code
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取状态描述
     *
     * @return 状态中文描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 根据数据库 code 反解析枚举
     *
     * @param code 数据库存储值
     * @return UploadStatus 枚举
     * @throws IllegalArgumentException 未知状态码时抛出异常
     */
    public static UploadStatus fromCode(int code) {
        for (UploadStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown UploadStatus code: " + code);
    }

    /**
     * 是否为终态
     * <p>
     * 终态包括：
     * - 上传成功
     * - 上传失败
     *
     * @return 是否终态
     */
    public boolean isFinal() {
        return this == SUCCESS || this == FAILED;
    }
}