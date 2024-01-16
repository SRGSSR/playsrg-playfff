import { Component, OnInit } from '@angular/core';
import { DeeplinkReport } from '../models/deeplink-report.model';
import { DeeplinkService } from './deeplink.service';

@Component({
  selector: 'app-deeplink',
  templateUrl: './deeplink.component.html',
  styleUrls: ['./deeplink.component.css']
})

export class DeeplinkComponent implements OnInit {
  deeplinkReports: DeeplinkReport[] = [];
  columns: string[] = [];

  constructor(private deeplinkService: DeeplinkService) { }

  ngOnInit() {
    this.columns = this.deeplinkService.getColumns();

    this.deeplinkService.getDeeplinkReports()
      .subscribe( data => {
        console.log(data);
        this.deeplinkReports = data;
      });
  };

  deleteDeeplinkReport(deeplinkReport: DeeplinkReport): void {
    this.deeplinkService.deleteDeeplinkReport(deeplinkReport)
      .subscribe( data => {
        this.deeplinkReports = this.deeplinkReports.filter(dr => dr !== deeplinkReport);
      })
  };


  packageNameFromId(id: string|number): string|number {
    if (typeof id === "number") {
      return id;
    }

    const idPrefixes: { [key: string]: string } = {
      "ch.rsi.player": "Play RSI Android",
      "ch.rsi.rsiplayer": "Play RSI iOS",
      "ch.srgssr.rsiplayer": "Play RSI iOS",
      "ch.rtr.player": "Play RTR Android",
      "ch.rtr.rtrplayer": "Play RTR iOS",
      "ch.srgssr.rtrplayer": "Play RTR iOS",
      "ch.rts.player": "Play RTS Android",
      "ch.rts.rtsplayer": "Play RTS iOS",
      "ch.srgssr.rtsplayer": "Play RTS iOS",
      "ch.srf.player": "Play SRF Android",
      "ch.srf.mobile.srfplayer": "Play SRF Android",
      "ch.srf.srfplayer": "Play SRF iOS",
      "ch.srgssr.srfplayer": "Play SRF iOS",
      "ch.swi.player": "Play SWI Android",
      "ch.swi.swiplayer": "Play SWI iOS",
      "ch.srgssr.swiplayer": "Play SWI iOS"
    };

    for (const prefix in idPrefixes) {
      if (id === prefix || id.startsWith(prefix + ".")) {
        return idPrefixes[prefix];
      }
    }

    return id;
  };
}
