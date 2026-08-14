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

    search_user,
    list_users,

    delete_user,
    make_admin,

    count_person,
    count_tasks,

    create_task,
    delete_task,
    my_created_tasks,
    my_executed_tasks,
    select_task,
    change_task_name,
    change_task_description,
    change_task_priority,
    change_task_status,

    execute_task,
    remove_executor,
    drop_task,

    search_task,
    list_task,
}
