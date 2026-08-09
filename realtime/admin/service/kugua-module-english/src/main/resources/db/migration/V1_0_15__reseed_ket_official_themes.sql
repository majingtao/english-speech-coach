-- =====================================================================
-- V1_0_15: 用 KET 官方 24 个主题分类替换 V1_0_12 的占位 15 主题
--   - Cambridge KET (A2 Key) 官方词汇主题分类
--   - 旧主题做软删除；旧 vocab_theme_rel 关联也清掉（防悬空）
--   - 新主题按官方字母序 sort 1-24
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) 软删除旧 15 个主题（V1_0_12 seed）
-- ---------------------------------------------------------------------
UPDATE esc_vocab_theme
SET deleted = b'1', update_time = CURRENT_TIMESTAMP
WHERE code IN (
    'sports', 'food', 'school', 'family', 'home', 'travel', 'hobbies',
    'technology', 'health', 'nature', 'clothes', 'transport',
    'weather', 'shopping', 'feelings'
)
  AND deleted = b'0';

-- 清理旧主题对应的 vocab × theme 关联（按 code 精确匹配，避开历史已软删的其它主题）
UPDATE esc_vocab_theme_rel
SET deleted = b'1', update_time = CURRENT_TIMESTAMP
WHERE theme_id IN (
    SELECT id FROM esc_vocab_theme
    WHERE code IN (
        'sports', 'food', 'school', 'family', 'home', 'travel', 'hobbies',
        'technology', 'health', 'nature', 'clothes', 'transport',
        'weather', 'shopping', 'feelings'
    )
)
  AND deleted = b'0';

-- ---------------------------------------------------------------------
-- 2) 插入 KET 官方 24 个主题（A2 Key vocabulary list — topic categories）
-- ---------------------------------------------------------------------
INSERT IGNORE INTO esc_vocab_theme (code, name_cn, name_en, level_code, sort, status) VALUES
    ('appliances',               '家用电器',                  'Appliances',                                                'ket',  1, 1),
    ('clothes-accessories',      '服饰与配件',                'Clothes and Accessories',                                   'ket',  2, 1),
    ('comm-tech',                '通讯与科技',                'Communication and Technology',                              'ket',  3, 1),
    ('documents-texts',          '文件与文本',                'Documents and Texts',                                       'ket',  4, 1),
    ('education',                '教育',                      'Education',                                                 'ket',  5, 1),
    ('entertainment-media',      '娱乐与媒体',                'Entertainment and Media',                                   'ket',  6, 1),
    ('family-friends',           '家人与朋友',                'Family and Friends',                                        'ket',  7, 1),
    ('food-drink',               '饮食',                      'Food and Drink',                                            'ket',  8, 1),
    ('health-medicine-exercise', '健康、医疗与运动',          'Health, Medicine and Exercise',                             'ket',  9, 1),
    ('hobbies-leisure',          '爱好与休闲',                'Hobbies and Leisure',                                       'ket', 10, 1),
    ('house-home',               '家与住所',                  'House and Home',                                            'ket', 11, 1),
    ('measurements',             '计量单位',                  'Measurements',                                              'ket', 12, 1),
    ('personal-feelings',        '个人感受、观点与经历（形容词）', 'Personal Feelings, Opinions and Experiences (adjectives)', 'ket', 13, 1),
    ('places-buildings',         '场所：建筑',                'Places: Buildings',                                         'ket', 14, 1),
    ('places-countryside',       '场所：乡村',                'Places: Countryside',                                       'ket', 15, 1),
    ('places-town-city',         '场所：城镇',                'Places: Town and City',                                     'ket', 16, 1),
    ('services',                 '服务',                      'Services',                                                  'ket', 17, 1),
    ('shopping',                 '购物',                      'Shopping',                                                  'ket', 18, 1),
    ('sport',                    '运动',                      'Sport',                                                     'ket', 19, 1),
    ('natural-world',            '自然世界',                  'The Natural World',                                         'ket', 20, 1),
    ('time',                     '时间',                      'Time',                                                      'ket', 21, 1),
    ('travel-transport',         '旅行与交通',                'Travel and Transport',                                      'ket', 22, 1),
    ('weather',                  '天气',                      'Weather',                                                   'ket', 23, 1),
    ('work-jobs',                '工作与职业',                'Work and Jobs',                                             'ket', 24, 1);
