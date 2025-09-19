import {get, post} from '/@/utils/http/axios';

enum URL {
    list = '/api/complaint/list',
    create = '/api/complaint/create',
    delete = '/api/complaint/delete',
    update = '/api/complaint/update',
    detail = '/api/complaint/detail',
    listUserComplaints = '/api/complaint/listUserComplaints',
    listByStatus = '/api/complaint/listByStatus',
    listByType = '/api/complaint/listByType',
    reply = '/api/complaint/reply'
}

const listApi = async (params: any) => get<any>({url: URL.list, params: params, data: {}, headers: {}});
const createApi = async (data: any) => post<any>({
    url: URL.create,
    params: {},
    data: data,
    headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
});
const deleteApi = async (params: any) => post<any>({url: URL.delete, params: params, headers: {}});
const updateApi = async (data: any) => post<any>({
    url: URL.update,
    params: {},
    data: data,
    headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
});
const detailApi = async (params: any) => get<any>({url: URL.detail, params: params, data: {}, headers: {}});
const listUserComplaintsApi = async (params: any) => get<any>({url: URL.listUserComplaints, params: params, data: {}, headers: {}});
const listByStatusApi = async (params: any) => get<any>({url: URL.listByStatus, params: params, data: {}, headers: {}});
const listByTypeApi = async (params: any) => get<any>({url: URL.listByType, params: params, data: {}, headers: {}});
const replyApi = async (data: any) => post<any>({
    url: URL.reply,
    params: {},
    data: data,
    headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
});

export {listApi, createApi, deleteApi, updateApi, detailApi, listUserComplaintsApi, listByStatusApi, listByTypeApi, replyApi};