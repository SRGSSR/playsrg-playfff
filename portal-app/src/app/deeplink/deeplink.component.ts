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

    if (id === "ch.rsi.player" || id.startsWith("ch.rsi.player.")) {
      return "Play RSI Android";
    } else if (id === "ch.rsi.rsiplayer" || id.startsWith("ch.rsi.rsiplayer.")) {
      return "Play RSI iOS";

    } else if (id === "ch.rtr.player" || id.startsWith("ch.rtr.player.")) {
      return "Play RTR Android";
    } else if (id === "ch.rtr.rtrplayer" || id.startsWith("ch.rtr.rtrplayer.")) {
      return "Play RTR iOS";

    } else if (id === "ch.rts.player" || id.startsWith("ch.rts.player.")) {
      return "Play RTS Android";
    } else if (id === "ch.rts.rtsplayer" || id.startsWith("ch.rts.rtsplayer.")) {
      return "Play RTS iOS";

    } else if (id === "ch.srf.player" || id.startsWith("ch.srf.player.") || id === "ch.srf.mobile.srfplayer" || id.startsWith("ch.srf.mobile.srfplayer.")) {
      return "Play SRF Android";
    } else if (id === "ch.srf.srfplayer" || id.startsWith("ch.srf.srfplayer.")) {
      return "Play SRF iOS";

    } else if (id === "ch.swi.player" || id.startsWith("ch.swi.player.")) {
      return "Play SWI Android";
    } else if (id === "ch.swi.swiplayer" || id.startsWith("ch.swi.swiplayer.")) {
      return "Play SWI iOS";

    } else {
      return id;
    }
  };
}
