(ns ^:fighweel-always flowtext.token.core)

(defn- insert-char [string char idx]
  (str (subs string 0 idx) char (subs string idx)))

(defn token-insert [{:keys [content] :as token } key offset]
  (assoc token :content (insert-char content key offset)))

(defn token-concat [x y]
  (assoc x :content (str (:content x) (:content y))))

(defn remove-char
  [content offset]
  (let [length (count content)]
    (str (subs content 0 (dec offset))
         (subs content offset length))))

(defn token-remove [{:keys [content] :as token } offset]
  (-> token
      (assoc :content (remove-char content offset))))

(defn count-tokens [line]
  (count (seq (:tokens line))))
