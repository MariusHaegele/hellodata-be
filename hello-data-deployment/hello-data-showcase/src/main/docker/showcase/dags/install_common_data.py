from airflow import DAG
from airflow.sensors.sql import SqlSensor
from airflow.operators.postgres_operator import PostgresOperator
from airflow.utils.dates import days_ago

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
}

dag = DAG(
    'install_common_data_' + "DD_KEY",
    default_args=default_args,
    description='A DAG to execute an SQL script',
    schedule_interval='@daily',
    start_date=days_ago(1),
    catchup=False
)

# Operator to execute the SQL script
dim_time_lzn = PostgresOperator(
    task_id='create_dim_time_lzn',
    postgres_conn_id='CONNECTION_ID',
    sql='sql/common_data/lzn/dim_time.sql',
    dag=dag
)

dim_time_udm = PostgresOperator(
    task_id='create_dim_time_udm',
    postgres_conn_id='CONNECTION_ID',
    sql='sql/common_data/udm/dim_time.sql',
    dag=dag
)

dim_time_lzn
dim_time_udm
