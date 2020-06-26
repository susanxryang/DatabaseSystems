from sys import exit
from checks import check, check_files_exist, check_sqlite_commands

query_files = [f"hw2-q{q}.sql" for q in range(1, 8)]
create_tables = "create-tables.sql"

def check_file_locations(error):
  check_files_exist(error, [create_tables] + query_files)

def check_sqlite_commands_queries(error):
  check_sqlite_commands(error, query_files, should_have_sqlite_commands=False)

def check_sqlite_commands_create_tables(error):
  check_sqlite_commands(error, [create_tables], should_have_sqlite_commands=True)

def main():
  print("Hi, I'm a submission checker. I will perform some checks to ensure that you're submitting your files correctly.")
  print("  (note: these checks are not guaranteed to be comprehensive; please read the spec carefully)")
  print()

  num_errors = check("files are in the correct location", check_file_locations)
  if num_errors:
    exit(1)

  num_errors += check("queries don't have SQLite commands", check_sqlite_commands_queries)
  num_errors += check("create-tables has SQLite commands", check_sqlite_commands_create_tables)
  if num_errors > 0:
    exit(1)

main()
