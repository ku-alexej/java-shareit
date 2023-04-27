#export POSTGRES_HOST=localhost
#export POSTGRES_PORT=6541
#export POSTGRES_USERNAME=shareit
#export PGPASSWORD=shareit
#
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "delete from comments cascade;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "delete from bookings cascade;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "delete from items cascade;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "delete from requests cascade;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "delete from users cascade;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "ALTER SEQUENCE comments_id_seq RESTART WITH 1;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "ALTER SEQUENCE bookings_id_seq RESTART WITH 1;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "ALTER SEQUENCE items_id_seq RESTART WITH 1;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "ALTER SEQUENCE requests_id_seq RESTART WITH 1;"
#psql -h ${POSTGRES_HOST} -U ${POSTGRES_USERNAME} -p ${POSTGRES_PORT} -c "ALTER SEQUENCE users_id_seq RESTART WITH 1;"
