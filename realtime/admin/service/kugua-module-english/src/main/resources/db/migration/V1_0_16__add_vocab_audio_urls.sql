-- =====================================================================
-- V1_0_16: 词汇音频字段
--   - 英式 / 美式 发音 URL 列（懒生成，由 LLM/Edge-TTS + yudao infra 文件服务托管）
--   - ASR 参考音暂不做（如需后期再加 audio_asr_url）
-- =====================================================================

ALTER TABLE esc_vocab
    ADD COLUMN audio_uk_url VARCHAR(255) DEFAULT NULL COMMENT '英式发音音频 URL（懒生成，存 yudao infra 文件服务）',
    ADD COLUMN audio_us_url VARCHAR(255) DEFAULT NULL COMMENT '美式发音音频 URL（懒生成）';
