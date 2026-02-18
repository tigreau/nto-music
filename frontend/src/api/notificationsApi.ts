import { API_BASE, fetchVoid, type OperationPath } from '@/api/core';

export function markNotificationRead(id: number): Promise<void> {
  const pathParams: OperationPath<'markAsRead'> = { notificationId: id };
  return fetchVoid(`${API_BASE}/notifications/${pathParams.notificationId}/read`, { method: 'PATCH' });
}

export function markAllNotificationsRead(): Promise<void> {
  return fetchVoid(`${API_BASE}/notifications/read-all`, { method: 'PATCH' });
}

export function deleteNotification(id: number): Promise<void> {
  const pathParams: OperationPath<'deleteNotification'> = { notificationId: id };
  return fetchVoid(`${API_BASE}/notifications/${pathParams.notificationId}`, { method: 'DELETE' });
}
