using System;
using System.Collections.ObjectModel;
using System.IO;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;

namespace TxTorBrowser.Services
{
    public enum DownloadState
    {
        Queued,
        Downloading,
        Paused,
        Completed,
        Failed
    }

    public class DownloadItem
    {
        public string Id { get; set; } = Guid.NewGuid().ToString();
        public string FileName { get; set; } = string.Empty;
        public string Url { get; set; } = string.Empty;
        public string DestinationPath { get; set; } = string.Empty;
        public long TotalBytes { get; set; }
        public long DownloadedBytes { get; set; }
        public DownloadState State { get; set; } = DownloadState.Queued;
        public double SpeedBytesPerSec { get; set; }
        public string ErrorMessage { get; set; } = string.Empty;
    }

    public class DownloadManager
    {
        private readonly HttpClient _client = new HttpClient();
        public ObservableCollection<DownloadItem> Tasks { get; } = new ObservableCollection<DownloadItem>();

        public async Task StartDownloadAsync(string url, string suggestedName, CancellationToken cancellationToken = default)
        {
            var downloadsFolder = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.UserProfile), "Downloads");
            var cleanName = string.IsNullOrWhiteSpace(suggestedName) ? "downloaded_file" : suggestedName;
            var destPath = Path.Combine(downloadsFolder, cleanName);

            var item = new DownloadItem
            {
                Url = url,
                FileName = cleanName,
                DestinationPath = destPath,
                State = DownloadState.Downloading
            };
            Tasks.Add(item);

            try
            {
                using var response = await _client.GetAsync(url, HttpCompletionOption.ResponseHeadersRead, cancellationToken);
                response.EnsureSuccessStatusCode();

                item.TotalBytes = response.Content.Headers.ContentLength ?? -1;

                using var stream = await response.Content.ReadAsStreamAsync(cancellationToken);
                using var fileStream = new FileStream(destPath, FileMode.Create, FileAccess.Write, FileShare.None, 8192, true);

                var buffer = new byte[32768];
                int read;
                while ((read = await stream.ReadAsync(buffer, 0, buffer.Length, cancellationToken)) > 0)
                {
                    await fileStream.WriteAsync(buffer, 0, read, cancellationToken);
                    item.DownloadedBytes += read;
                }

                item.State = DownloadState.Completed;
            }
            catch (Exception ex)
            {
                item.State = DownloadState.Failed;
                item.ErrorMessage = ex.Message;
            }
        }
    }
}
