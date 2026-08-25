package ClientHandler;

public enum SetOfCommand {
    help,
    exit,

    register,
    login,
    logout,
    delete_account,
    change_login,
    change_password,
    change_phone_number,
    change_email,
    change_secret_word,
    my_info,

    search_user, //combo
    list_users,
    user_count,

    delete_user, //combo
    make_admin, //combo

    create_task,
    delete_task, //
    my_created_tasks,
    my_executed_tasks,
    select_task, //combo
    change_task_name, //
    change_task_description, //
    change_task_priority, //
    change_task_status, //

    execute_task, //
    remove_executor, //
    drop_task, //

    search_task_by_creator, //combo
    search_task_by_executor, //combo
    search_task_by_name, //combo
    search_task_by_priority, //combo
    search_task_by_status, //combo
    sort_task_created_between, //combo
    sort_task_updated_between, //combo
    list_tasks,
    task_count
}
