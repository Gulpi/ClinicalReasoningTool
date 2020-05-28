-- add new tmp column

ALTER TABLE CRT.RELATION_PROBLEM
 ADD (syn2_id  NUMBER                               DEFAULT 0);
 
-- update values

update crt.relation_problem set syn2_id = to_number(syn_id) where syn_id is not null and syn_id <> '0'

-- install trigger for auto filling of the new column

CREATE OR REPLACE TRIGGER CRT.RELATION_PROBLEM_syn_id_Trg
BEFORE INSERT OR UPDATE
ON CRT.RELATION_PROBLEM REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
BEGIN 
   IF (:new.syn_id is not null ) THEN
      :new.syn2_id := to_number(:new.syn_id);
   END IF;
   exception when others then :new.syn2_id := 0;
END;
/

-- update view

DROP VIEW CRT.VP_LISTITEM_VIEW;

/* Formatted on 5/25/2020 6:07:24 AM (QP5 v5.336) */
CREATE OR REPLACE FORCE VIEW CRT.VP_LISTITEM_VIEW
(
    VP_ID,
    SOURCE_ID,
    SYNONYM_ID,
    SEL_SYN_ID,
    NAME,
    ITEM_TYPE,
    ITEM_SYNONYM,
    PIS_ID
)
AS
    SELECT pis.vp_id,
           rp.source_id,
           -1,
           -1,
           cl.NAME,
           1,
           0,
           pis.id
      FROM patient_illnessscript pis, relation_problem rp, clinreason_list cl
     WHERE     pis.TYPE = 2
           AND pis.delete_flag = 0
           AND pis.ID = rp.dest_id
           AND (rp.source_id = cl.item_id)
           AND cl.ignore = 0
    UNION
    SELECT pis.vp_id,
           rp.source_id,
           cl.id,
           rp.syn2_id,
           cl.NAME,
           1,
           1,
           pis.id
      FROM patient_illnessscript  pis,
           relation_problem       rp,
           clinreason_list_syn    cl
     WHERE     pis.TYPE = 2
           AND pis.delete_flag = 0
           AND pis.ID = rp.dest_id
           AND (rp.source_id = cl.item_id)
           AND cl.ignore = 0
    UNION
    SELECT pis.vp_id,
           rp.source_id,
           -1,
           -1,
           cl.NAME,
           2,
           0,
           pis.id
      FROM patient_illnessscript  pis,
           relation_diagnosis     rp,
           clinreason_list        cl
     WHERE     pis.TYPE = 2
           AND pis.delete_flag = 0
           AND pis.ID = rp.dest_id
           AND (rp.source_id = cl.item_id)
           AND cl.ignore = 0
    UNION
    SELECT pis.vp_id,
           rp.source_id,
           cl.id,
           rp.syn_id,
           cl.NAME,
           2,
           1,
           pis.id
      FROM patient_illnessscript  pis,
           relation_diagnosis     rp,
           clinreason_list_syn    cl
     WHERE     pis.TYPE = 2
           AND pis.delete_flag = 0
           AND pis.ID = rp.dest_id
           AND (rp.source_id = cl.item_id)
           AND cl.ignore = 0
    UNION
    SELECT pis.vp_id,
           rp.source_id,
           -1,
           -1,
           cl.NAME,
           3,
           0,
           pis.id
      FROM patient_illnessscript pis, relation_test rp, clinreason_list cl
     WHERE     pis.TYPE = 2
           AND pis.delete_flag = 0
           AND pis.ID = rp.dest_id
           AND (rp.source_id = cl.item_id)
           AND cl.ignore = 0
    UNION
    SELECT pis.vp_id,
           rp.source_id,
           cl.id,
           rp.syn_id,
           cl.NAME,
           3,
           1,
           pis.id
      FROM patient_illnessscript  pis,
           relation_test          rp,
           clinreason_list_syn    cl
     WHERE     pis.TYPE = 2
           AND pis.delete_flag = 0
           AND pis.ID = rp.dest_id
           AND (rp.source_id = cl.item_id)
           AND cl.ignore = 0
    UNION
    SELECT pis.vp_id,
           rp.source_id,
           -1,
           -1,
           cl.NAME,
           4,
           0,
           pis.id
      FROM patient_illnessscript  pis,
           relation_management    rp,
           clinreason_list        cl
     WHERE     pis.TYPE = 2
           AND pis.delete_flag = 0
           AND pis.ID = rp.dest_id
           AND (rp.source_id = cl.item_id)
           AND cl.ignore = 0
    UNION
    SELECT pis.vp_id,
           rp.source_id,
           cl.id,
           rp.syn_id,
           cl.NAME,
           4,
           1,
           pis.id
      FROM patient_illnessscript  pis,
           relation_management    rp,
           clinreason_list_syn    cl
     WHERE     pis.TYPE = 2
           AND pis.delete_flag = 0
           AND pis.ID = rp.dest_id
           AND (rp.source_id = cl.item_id)
           AND cl.ignore = 0;

COMMENT ON COLUMN CRT.VP_LISTITEM_VIEW.VP_ID IS 'vp';

COMMENT ON COLUMN CRT.VP_LISTITEM_VIEW.SOURCE_ID IS 'item_id';

COMMENT ON COLUMN CRT.VP_LISTITEM_VIEW.NAME IS 'item name';

COMMENT ON COLUMN CRT.VP_LISTITEM_VIEW.ITEM_TYPE IS '1=problem, 2=diagonsis, 3=test,4=management';

COMMENT ON COLUMN CRT.VP_LISTITEM_VIEW.ITEM_SYNONYM IS '0 for main item, 1 for synonyns';

COMMENT ON COLUMN CRT.VP_LISTITEM_VIEW.PIS_ID IS 'expert(s) patient illness script id';

