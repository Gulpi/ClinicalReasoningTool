--
-- Create Schema Script 
--   Database Version   : 11.2.0.2.0 
--   TOAD Version       : 9.5.0.31 
--   DB Connect String  : CASUS_NEW 
--   Schema             : CRT 
--   Script Created by  : CRT 
--   Script Created at  : 28.12.2016 16:15:29 
--   Physical Location  :  
--   Notes              :  
--

-- Object Counts: 
--   Indexes: 12        Columns: 15         
--   Sequences: 3 
--   Tables: 22         Columns: 206        
--   Views: 1           


CREATE SEQUENCE CR_SEQ
  START WITH 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  NOCACHE
  NOORDER;


CREATE SEQUENCE LIST_SEQ
  START WITH 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  NOCACHE
  NOORDER;


CREATE SEQUENCE USER_SEQ
  START WITH 1
  MAXVALUE 9999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  NOCACHE
  NOORDER;


CREATE TABLE CONNECTION
(
  ID             NUMBER,
  TARGET_ID      NUMBER,
  ILLSCRIPT_ID   NUMBER,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  START_ID       NUMBER,
  START_TYPE     NUMBER,
  TARGET_TYPE    NUMBER,
  WEIGHT         NUMBER                         DEFAULT -1,
  STAGE          NUMBER                         DEFAULT -1,
  START_EP_IDX   NUMBER                         DEFAULT 0,
  TARGET_EP_IDX  NUMBER                         DEFAULT 0
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE CRUSER
(
  USER_ID        NUMBER,
  USER_ID_EXT    VARCHAR2(200 BYTE)             DEFAULT -1,
  SYSTEM_ID      NUMBER                         DEFAULT -1,
  FIRSTNAME      VARCHAR2(200 BYTE),
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  USERNAME       VARCHAR2(100 BYTE),
  PASSWORD       VARCHAR2(100 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE ERROR
(
  ID             NUMBER,
  DEST_ID        NUMBER,
  TYPE           NUMBER,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  DISCR          VARCHAR2(5 BYTE),
  STAGE          NUMBER                         DEFAULT -1,
  IDX            NUMBER                         DEFAULT 0
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE FEEDBACK
(
  ID               NUMBER,
  PATILLSCRIPT_ID  NUMBER,
  FEEDBACK_TYPE    NUMBER,
  TASK_TYPE        NUMBER,
  STAGE            NUMBER,
  CREATION_DATE    DATE                         DEFAULT SYSDATE
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE LOG
(
  SESSION_ID       NUMBER                       DEFAULT -1,
  ACTION           NUMBER,
  CREATION_DATE    DATE                         DEFAULT SYSDATE,
  ID               NUMBER,
  SOURCE_ID        NUMBER,
  SOURCE_ID2       NUMBER                       DEFAULT -1,
  STAGE            NUMBER                       DEFAULT -1,
  PATILLSCRIPT_ID  NUMBER                       DEFAULT -1
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE PARENT_SYSTEM
(
  INTERNAL_ID  NUMBER,
  SYTEM_NAME   VARCHAR2(200 BYTE),
  SYSTEM_ID    VARCHAR2(200 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE PATIENT_ILLNESSSCRIPT
(
  ID                 NUMBER,
  COURSEOFTIME       NUMBER,
  PATIENT_ID         NUMBER,
  CREATION_DATE      DATE                       DEFAULT SYSDATE,
  DIAGNOSIS_ID       NUMBER,
  TYPE               NUMBER,
  USER_ID            NUMBER,
  LOCALE             VARCHAR2(20 BYTE),
  SUMMST_ID          NUMBER                     DEFAULT -1,
  PARENT_ID          NUMBER                     DEFAULT -1,
  STAGE              NUMBER                     DEFAULT -1,
  DDX_SUBMITTED      NUMBER                     DEFAULT 0,
  CONFIDENCE         NUMBER                     DEFAULT -1,
  PEER_SYNC          NUMBER                     DEFAULT 0,
  MAX_DDX_SUBMITTED  NUMBER                     DEFAULT -1,
  LAST_ACCESS_DATE   DATE                       DEFAULT SYSDATE,
  VP_ID              VARCHAR2(50 BYTE),
  SHOW_SOL           NUMBER                     DEFAULT -1,
  EXT_UID            VARCHAR2(200 BYTE),
  DELETE_FLAG        NUMBER                     DEFAULT 0
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE PEER
(
  ACTION         NUMBER                         DEFAULT -1,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  CHANGE_DATE    DATE                           DEFAULT SYSDATE,
  ITEM_ID        NUMBER                         DEFAULT -1,
  PEER_NUM       NUMBER                         DEFAULT 0,
  ID             NUMBER,
  STAGE          NUMBER                         DEFAULT -1,
  PARENT_ID      NUMBER                         DEFAULT -1,
  SCORE_SUM      FLOAT(126)                     DEFAULT 0,
  VP_ID          VARCHAR2(50 BYTE),
  ORG_SCORE_EXP  NUMBER                         DEFAULT 0,
  SCORE_EXP      NUMBER                         DEFAULT 0
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE RELATION_DIAGNOSIS
(
  SOURCE_ID      NUMBER,
  DEST_ID        NUMBER,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  ID             NUMBER,
  IDX            NUMBER,
  X              NUMBER,
  Y              NUMBER,
  COLOR          VARCHAR2(50 BYTE)              DEFAULT '#ffffff',
  MNM            NUMBER                         DEFAULT 0,
  STAGE          NUMBER                         DEFAULT 1,
  SYN_ID         NUMBER                         DEFAULT -1,
  TIER           NUMBER                         DEFAULT 0,
  RULED_OUT      NUMBER                         DEFAULT 0,
  WORKING_DDX    NUMBER                         DEFAULT -1,
  PREFIX         VARCHAR2(50 BYTE),
  FINAL_DDX      NUMBER                         DEFAULT -1
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE RELATION_MANAGEMENT
(
  SOURCE_ID      NUMBER,
  DEST_ID        NUMBER,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  ID             NUMBER,
  IDX            NUMBER,
  X              NUMBER,
  Y              NUMBER,
  STAGE          NUMBER                         DEFAULT 1,
  SYN_ID         NUMBER                         DEFAULT -1,
  PREFIX         VARCHAR2(50 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE RELATION_PROBLEM
(
  SOURCE_ID      NUMBER,
  DEST_ID        NUMBER,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  ID             NUMBER,
  IDX            NUMBER,
  X              NUMBER,
  Y              NUMBER,
  SYN_ID         VARCHAR2(50 BYTE),
  STAGE          NUMBER                         DEFAULT 1,
  PREFIX         VARCHAR2(50 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE RELATION_TEST
(
  SOURCE_ID      NUMBER,
  DEST_ID        NUMBER,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  ID             NUMBER,
  IDX            NUMBER,
  X              NUMBER,
  Y              NUMBER,
  STAGE          NUMBER                         DEFAULT 1,
  SYN_ID         NUMBER                         DEFAULT -1,
  PREFIX         VARCHAR2(50 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE SCORE
(
  ID                       NUMBER,
  PATIENTILLNESSSCRIPT_ID  NUMBER,
  SCORE                    FLOAT(126)           DEFAULT -1,
  ACTION_ITEM_ID           NUMBER,
  CREATION_DATE            DATE                 DEFAULT SYSDATE,
  WEIGHT                   NUMBER               DEFAULT 1,
  ITEM_TYPE                NUMBER,
  SCORE_EXP                FLOAT(126)           DEFAULT -1,
  SCORE_PEER               FLOAT(126)           DEFAULT -1,
  SCORE_ILLSCR             FLOAT(126)           DEFAULT -1,
  TIMING                   NUMBER               DEFAULT -1,
  FEEDBACK                 NUMBER               DEFAULT 0,
  STAGE                    NUMBER               DEFAULT -1,
  EXPITEM_ID               NUMBER               DEFAULT -1,
  ORG_SCORE_EXP            NUMBER               DEFAULT -1,
  USER_ID                  NUMBER               DEFAULT -1,
  PARENT_ID                NUMBER               DEFAULT -1,
  VP_ID                    VARCHAR2(50 BYTE),
  DISTANCE                 NUMBER               DEFAULT -99,
  DELETE_FLAG              NUMBER               DEFAULT 0
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE SEMANTIC_QUALIFIERS
(
  ID           NUMBER                           DEFAULT -1,
  TEXT         VARCHAR2(200 BYTE),
  CATEGORY     NUMBER                           DEFAULT -1,
  LANGUAGE     VARCHAR2(2 BYTE),
  CONTRASTS    NUMBER                           DEFAULT -1,
  SYNONYMAOF   NUMBER                           DEFAULT -1,
  ADDED        NUMBER                           DEFAULT -1,
  DELETE_FLAG  NUMBER                           DEFAULT 0
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE SUMMSTATEMENT
(
  ID               NUMBER,
  TEXT             VARCHAR2(4000 BYTE),
  CREATION_DATE    DATE                         DEFAULT SYSDATE,
  STAGE            NUMBER                       DEFAULT -1,
  PATILLSCRIPT_ID  NUMBER                       DEFAULT -1,
  ANALYZED         NUMBER                       DEFAULT 0,
  TYPE             NUMBER                       DEFAULT 1,
  LANG             VARCHAR2(2 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE TYPEAHEAD
(
  USER_ID        NUMBER                         DEFAULT -1,
  VP_ID          VARCHAR2(20 BYTE)              DEFAULT -1,
  TEXT           VARCHAR2(500 BYTE),
  FINAL_ITEM_ID  NUMBER                         DEFAULT -1,
  ID             NUMBER                         DEFAULT -1,
  SCRIPT_ID      NUMBER                         DEFAULT -1,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  TYPE           NUMBER                         DEFAULT -1
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE VPID_SYSTEM
(
  VP_ID      NUMBER,
  SYSTEM_ID  NUMBER,
  ID         NUMBER,
  VP_NAME    VARCHAR2(500 BYTE),
  PARENT_ID  VARCHAR2(50 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE LEARNING
(
  ID               NUMBER,
  PATILLSCRIPT_ID  NUMBER,
  VP_ID            VARCHAR2(50 BYTE),
  ACTIVE_LEARNING  FLOAT(126)                   DEFAULT 0,
  SCAFFOLDING      FLOAT(126)                   DEFAULT 0,
  USER_ID          NUMBER                       DEFAULT 0,
  CREATION_DATE    DATE                         DEFAULT SYSDATE,
  TIP              NUMBER                       DEFAULT -1,
  DELETE_FLAG      NUMBER                       DEFAULT 0,
  SUMMST           NUMBER                       DEFAULT -1
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE SUMSTATEMENT_SQ
(
  SUMST_ID       NUMBER,
  SQ_ID          NUMBER,
  CREATION_DATE  DATE                           DEFAULT SYSDATE,
  ID             NUMBER,
  TEXT           VARCHAR2(100 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE CLINREASON_LIST_CODES
(
  ITEM_ID  NUMBER,
  CODE     VARCHAR2(100 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE CLINREASON_LIST
(
  ITEM_ID           NUMBER                      DEFAULT -1,
  LANGUAGE          VARCHAR2(5 BYTE),
  NAME              VARCHAR2(100 BYTE),
  ITEM_NOTE         VARCHAR2(2000 BYTE),
  ITEM_TYPE         VARCHAR2(10 BYTE)           DEFAULT -1,
  ITEM_LEVEL        NUMBER                      DEFAULT -1,
  CODE              VARCHAR2(100 BYTE),
  MESH_UI           VARCHAR2(20 BYTE),
  SOURCE            VARCHAR2(20 BYTE),
  ITEM_DESCRIPTION  VARCHAR2(2000 BYTE),
  MESH_EC           VARCHAR2(500 BYTE),
  MESH_CATEGORY     VARCHAR2(500 BYTE),
  IGNORE            NUMBER                      DEFAULT 0
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE CLINREASON_LIST_SYN
(
  ITEM_ID        NUMBER,
  NAME           VARCHAR2(1000 BYTE),
  ID             NUMBER                         DEFAULT -1,
  LANG           VARCHAR2(2 BYTE)               DEFAULT 'EN',
  RATING_WEIGHT  FLOAT(126)                     DEFAULT 1,
  SOURCE         VARCHAR2(10 BYTE)              DEFAULT 'MESH',
  IGNORE         NUMBER                         DEFAULT 0
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE INDEX LOG_SCRIPTID_IDX ON LOG
(PATILLSCRIPT_ID)
LOGGING
NOPARALLEL;


CREATE INDEX SYN_ITEMID_IDX ON CLINREASON_LIST_SYN
(ITEM_ID)
LOGGING
NOPARALLEL;


CREATE INDEX SCORE_ILLSCRIPTID_IDX ON SCORE
(PATIENTILLNESSSCRIPT_ID)
LOGGING
NOPARALLEL;


CREATE INDEX CR_LIST_ID_IDX ON CLINREASON_LIST
(ITEM_ID)
LOGGING
NOPARALLEL;


CREATE INDEX CLINREASON_LIST_TYPE_IDX ON CLINREASON_LIST
(ITEM_TYPE)
LOGGING
NOPARALLEL;


CREATE INDEX ERROR_DESTID_IDX ON ERROR
(DEST_ID)
LOGGING
NOPARALLEL;


CREATE INDEX PTIS_USRID_VPID_TYPE_IDX ON PATIENT_ILLNESSSCRIPT
(USER_ID, VP_ID, TYPE)
LOGGING
NOPARALLEL;


CREATE INDEX SCORE_USRID_IDX ON SCORE
(USER_ID)
LOGGING
NOPARALLEL;


CREATE INDEX SUMMST_ID_IDX ON SUMMSTATEMENT
(ID)
LOGGING
NOPARALLEL;


CREATE INDEX FEEDBACK_PISID_IDX ON FEEDBACK
(ID)
LOGGING
NOPARALLEL;


CREATE INDEX CR_CODE_LANG_IDX ON CLINREASON_LIST
(LANGUAGE, CODE)
LOGGING
NOPARALLEL;


CREATE INDEX SYN_ID_IDX ON CLINREASON_LIST_SYN
(ID)
LOGGING
NOPARALLEL;


CREATE OR REPLACE VIEW VP_LISTITEM_VIEW
AS 
SELECT pis.vp_id, rp.source_id, cl.NAME
     FROM patient_illnessscript pis, relation_problem rp, clinreason_list cl
    WHERE pis.TYPE = 2
      AND pis.delete_flag = 0
      AND pis.ID = rp.dest_id
      AND (rp.source_id = cl.item_id)
   UNION
   SELECT pis.vp_id, rp.source_id, cl.NAME
     FROM patient_illnessscript pis,
          relation_problem rp,
          clinreason_list_syn cl
    WHERE pis.TYPE = 2
      AND pis.delete_flag = 0
      AND cl.item_id = 0
      AND pis.ID = rp.dest_id
      AND (rp.source_id = cl.item_id)
   UNION
   SELECT pis.vp_id, rp.source_id, cl.NAME
     FROM patient_illnessscript pis, relation_diagnosis rp,
          clinreason_list cl
    WHERE pis.TYPE = 2
      AND pis.delete_flag = 0
      AND pis.ID = rp.dest_id
      AND (rp.source_id = cl.item_id)
   UNION
   SELECT pis.vp_id, rp.source_id, cl.NAME
     FROM patient_illnessscript pis,
          relation_diagnosis rp,
          clinreason_list_syn cl
    WHERE pis.TYPE = 2
      AND pis.delete_flag = 0
      AND cl.item_id = 0
      AND pis.ID = rp.dest_id
      AND (rp.source_id = cl.item_id)
   UNION
   SELECT pis.vp_id, rp.source_id, cl.NAME
     FROM patient_illnessscript pis, relation_test rp, clinreason_list cl
    WHERE pis.TYPE = 2
      AND pis.delete_flag = 0
      AND pis.ID = rp.dest_id
      AND (rp.source_id = cl.item_id)
   UNION
   SELECT pis.vp_id, rp.source_id, cl.NAME
     FROM patient_illnessscript pis, relation_test rp, clinreason_list_syn cl
    WHERE pis.TYPE = 2
      AND pis.delete_flag = 0
      AND cl.item_id = 0
      AND pis.ID = rp.dest_id
      AND (rp.source_id = cl.item_id)
   UNION
   SELECT pis.vp_id, rp.source_id, cl.NAME
     FROM patient_illnessscript pis,
          relation_management rp,
          clinreason_list cl
    WHERE pis.TYPE = 2
      AND pis.delete_flag = 0
      AND pis.ID = rp.dest_id
      AND (rp.source_id = cl.item_id)
   UNION
   SELECT pis.vp_id, rp.source_id, cl.NAME
     FROM patient_illnessscript pis,
          relation_management rp,
          clinreason_list_syn cl
    WHERE pis.TYPE = 2
      AND pis.delete_flag = 0
      AND cl.item_id = 0
      AND pis.ID = rp.dest_id
      AND (rp.source_id = cl.item_id);


