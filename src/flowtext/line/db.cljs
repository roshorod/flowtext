(ns ^:fighweel-always flowtext.line.db)

(def default-db
  {1 {:tokens {2 {:content "1"}, 1 {:content "2"}, 3 {:content "3"}}},
   0 {:tokens {2 {:content "first", :test "test"}, 1 {:content "second"},
               3 {:content "third"}}},
   4 {:tokens {3 {:content "first"}, 1 {:content "third"}}}
   3 {:tokens {1 {:content "One"}}}})
